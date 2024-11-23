-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 23, 2024 at 03:44 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chatbotdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `chats`
--

CREATE TABLE `chats` (
  `chat_id` int(11) NOT NULL,
  `client_message` text DEFAULT NULL,
  `client_datetime` datetime DEFAULT NULL,
  `bot_message` text DEFAULT NULL,
  `bot_datetime` datetime DEFAULT NULL,
  `history_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chats`
--

INSERT INTO `chats` (`chat_id`, `client_message`, `client_datetime`, `bot_message`, `bot_datetime`, `history_id`) VALUES
(1, 'hai', NULL, 'Halo! Universitas Mataram siap membantu Anda. Silakan tanyakan apa pun yang ingin Anda ketahui.', NULL, 16),
(2, 'halo', NULL, 'Selamat datang di website resmi Universitas Mataram', NULL, 17),
(3, 'halo', NULL, 'Hai! Apa kabar? Kami senang bisa membantu Anda di Universitas Mataram.', NULL, 18),
(4, 'halo', NULL, 'Selamat datang di Universitas Mataram! Apakah ada informasi yang Anda butuhkan?', NULL, 19),
(5, 'apa itu kkn', NULL, 'Kuliah Kerja Nyata (KKN) adalah suatu kegiatan perkuliahan dan kerja lapangan yang merupakan pengintegrasian dari pendidikan dan pengajaran', NULL, 19);

-- --------------------------------------------------------

--
-- Table structure for table `histories`
--

CREATE TABLE `histories` (
  `history_id` int(11) NOT NULL,
  `last_updated` datetime DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `histories`
--

INSERT INTO `histories` (`history_id`, `last_updated`, `created_on`, `user_id`) VALUES
(1, '2024-11-23 20:38:12', '2024-11-23 20:38:12', 5),
(2, '2024-11-23 20:51:58', '2024-11-23 20:51:58', 6),
(3, '2024-11-23 20:53:19', '2024-11-23 20:53:19', 6),
(4, '2024-11-23 20:56:46', '2024-11-23 20:56:46', 6),
(5, '2024-11-23 20:58:46', '2024-11-23 20:58:46', 6),
(6, '2024-11-23 21:00:12', '2024-11-23 21:00:12', 6),
(7, '2024-11-23 21:02:02', '2024-11-23 21:02:02', 6),
(8, '2024-11-23 21:05:10', '2024-11-23 21:05:10', 6),
(9, '2024-11-23 21:05:25', '2024-11-23 21:05:25', 5),
(10, '2024-11-23 21:09:10', '2024-11-23 21:09:10', 5),
(11, '2024-11-23 21:16:06', '2024-11-23 21:16:06', 6),
(12, '2024-11-23 22:02:11', '2024-11-23 22:02:11', 5),
(13, '2024-11-23 22:13:35', '2024-11-23 22:13:35', 5),
(14, '2024-11-23 22:18:20', '2024-11-23 22:18:20', 5),
(15, '2024-11-23 22:19:30', '2024-11-23 22:19:30', 5),
(16, '2024-11-23 22:23:16', '2024-11-23 22:23:16', 5),
(17, '2024-11-23 22:28:42', '2024-11-23 22:28:42', 5),
(18, '2024-11-23 22:31:47', '2024-11-23 22:31:47', 5),
(19, '2024-11-23 22:35:52', '2024-11-23 22:35:26', 5);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `user_id`, `password`) VALUES
('mausneg', 5, '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08'),
('Fiqar', 6, 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chats`
--
ALTER TABLE `chats`
  ADD PRIMARY KEY (`chat_id`),
  ADD KEY `chats___fk` (`history_id`);

--
-- Indexes for table `histories`
--
ALTER TABLE `histories`
  ADD PRIMARY KEY (`history_id`),
  ADD KEY `histories_clients_fk` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `users_pk_2` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chats`
--
ALTER TABLE `chats`
  MODIFY `chat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `histories`
--
ALTER TABLE `histories`
  MODIFY `history_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chats`
--
ALTER TABLE `chats`
  ADD CONSTRAINT `chats___fk` FOREIGN KEY (`history_id`) REFERENCES `histories` (`history_id`);

--
-- Constraints for table `histories`
--
ALTER TABLE `histories`
  ADD CONSTRAINT `histories_clients_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
