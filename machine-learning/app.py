import os
import tensorflow as tf
import tensorflow_model_analysis as tfma
from tfx.components import CsvExampleGen, StatisticsGen, SchemaGen, ExampleValidator, Transform, Trainer, Tuner, Evaluator, Pusher
from tfx.proto import example_gen_pb2, trainer_pb2, transform_pb2, pusher_pb2
from tfx.orchestration.experimental.interactive.interactive_context import InteractiveContext
from tfx.dsl.components.common.resolver import Resolver
from tfx.dsl.input_resolution.strategies.latest_blessed_model_strategy import LatestBlessedModelStrategy
from tfx.types import Channel
from tfx.types.standard_artifacts import Model, ModelBlessing
from tfx.orchestration.local.local_dag_runner import LocalDagRunner
from tfx.orchestration import pipeline, metadata

PIPELINE_NAME = 'chatbot_pipeline'
SCHEMA_PIPELINE_NAME = 'chatbot_schema_pipeline'
PIPELINE_ROOT = os.path.abspath(os.path.join('machine-learning','pipelines', PIPELINE_NAME))
METADATA_PATH = os.path.abspath(os.path.join('machine-learning','metadata', PIPELINE_NAME, 'metadata.db'))
SERVING_MODEL_DIR = os.path.abspath(os.path.join('machine-learning','serving_model_dir', 'chatbot-psti-model'))
DATA_ROOT = os.path.abspath(os.path.join('machine-learning', 'data', 'final'))

output = example_gen_pb2.Output(
    split_config=example_gen_pb2.SplitConfig(splits=[
        example_gen_pb2.SplitConfig.Split(name='train', hash_buckets=7),
        example_gen_pb2.SplitConfig.Split(name='eval', hash_buckets=3)
    ])
)

example_gen = CsvExampleGen(input_base=DATA_ROOT, output_config=output)

statistics_gen = StatisticsGen(examples=example_gen.outputs['examples'])

schema_gen = SchemaGen(statistics=statistics_gen.outputs['statistics'])

example_validator = ExampleValidator(
    statistics=statistics_gen.outputs['statistics'],
    schema=schema_gen.outputs['schema']
)

transform = Transform(
    examples=example_gen.outputs['examples'],
    schema=schema_gen.outputs['schema'],
    module_file=os.path.abspath(os.path.join('machine-learning','modules', 'transform.py'))
)

tuner = Tuner(
    module_file=os.path.abspath(os.path.join('machine-learning','modules', 'tuner.py')),
    examples=transform.outputs['transformed_examples'],
    transform_graph=transform.outputs['transform_graph'],   
    schema=schema_gen.outputs['schema'],
    train_args=trainer_pb2.TrainArgs(splits=['train']),
    eval_args=trainer_pb2.EvalArgs(splits=['eval']),
)

trainer = Trainer(
    module_file=os.path.abspath(os.path.join('machine-learning','modules', 'trainer.py')),
    examples=transform.outputs['transformed_examples'],
    transform_graph=transform.outputs['transform_graph'],
    schema=schema_gen.outputs['schema'],
    hyperparameters=tuner.outputs['best_hyperparameters'],
    train_args=trainer_pb2.TrainArgs(splits=['train']),
    eval_args=trainer_pb2.EvalArgs(splits=['eval'])
)

model_resolver = Resolver(
    strategy_class=LatestBlessedModelStrategy,
    model=Channel(type=Model),
    model_blessing=Channel(type=ModelBlessing)
).with_id('Latest_blessed_model_resolver')

eval_config = tfma.EvalConfig(
    model_specs=[tfma.ModelSpec(label_key='tags_xf')],
    slicing_specs=[tfma.SlicingSpec()],
    metrics_specs=[
        tfma.MetricsSpec(metrics=[
            tfma.MetricConfig(class_name='ExampleCount'),
            tfma.MetricConfig(class_name='CategoricalAccuracy',
                threshold=tfma.MetricThreshold(
                    value_threshold=tfma.GenericValueThreshold(
                        lower_bound={'value': 0.5}),
                    change_threshold=tfma.GenericChangeThreshold(
                        direction=tfma.MetricDirection.HIGHER_IS_BETTER,
                        absolute={'value': 0.0001})
                    )
            )
        ])
    ]
)

evaluator = Evaluator(
    examples=transform.outputs['transformed_examples'],
    model=trainer.outputs['model'],
    baseline_model=model_resolver.outputs['model'],
    eval_config=eval_config
)

pusher = Pusher(
    model=trainer.outputs['model'],
    model_blessing=evaluator.outputs['blessing'],
    push_destination=pusher_pb2.PushDestination(
        filesystem=pusher_pb2.PushDestination.Filesystem(
            base_directory=SERVING_MODEL_DIR
        )
    )
)

def create_pipeline():
    return pipeline.Pipeline(
        pipeline_name=PIPELINE_NAME,
        pipeline_root=PIPELINE_ROOT,
        components=[
            example_gen,
            statistics_gen,
            schema_gen,
            example_validator,
            transform,
            tuner,
            trainer,
            model_resolver,
            evaluator,
            pusher
        ],
        enable_cache=True,
        metadata_connection_config=metadata.sqlite_metadata_connection_config(METADATA_PATH)
    )

if __name__ == '__main__':
    LocalDagRunner().run(create_pipeline())