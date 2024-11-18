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

PIPELINE_NAME = 'chatbot_pipeline'
SCHEMA_PIPELINE_NAME = 'chatbot_schema_pipeline'
PIPELINE_ROOT = os.path.join('pipelines', PIPELINE_NAME)
METADATA_PATH = os.path.join('metadata', PIPELINE_NAME, 'metadata.db')
SERVING_MODEL_DIR = os.path.join('serving_model', PIPELINE_NAME)
DATA_ROOT = 'chatbot/data/final'

context = InteractiveContext()

output = example_gen_pb2.Output(
    split_config=example_gen_pb2.SplitConfig(splits=[
        example_gen_pb2.SplitConfig.Split(name='train', hash_buckets=7),
        example_gen_pb2.SplitConfig.Split(name='eval', hash_buckets=3)
    ])
)

example_gen = CsvExampleGen(input_base=DATA_ROOT, output_config=output)
context.run(example_gen)

statistics_gen = StatisticsGen(examples=example_gen.outputs['examples'])
context.run(statistics_gen)

schema_gen = SchemaGen(statistics=statistics_gen.outputs['statistics'])
context.run(schema_gen)

example_validator = ExampleValidator(
    statistics=statistics_gen.outputs['statistics'],
    schema=schema_gen.outputs['schema']
)

context.run(example_validator)




