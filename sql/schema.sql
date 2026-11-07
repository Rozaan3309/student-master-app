CREATE DATABASE IF NOT EXISTS university
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE university;

CREATE TABLE IF NOT EXISTS students (
    student_id INT          NOT NULL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO students (student_id, name, email) VALUES
    (1001, 'Aditya Lestari', 'aditya.lestari@students.campus.id'),
    (1002, 'Bunga Nugroho', 'bunga.nugroho@students.campus.id'),
    (1003, 'Citra Santoso', 'citra.santoso@students.campus.id'),
    (1004, 'Dimas Putra', 'dimas.putra@students.campus.id'),
    (1005, 'Eka Maharani', 'eka.maharani@students.campus.id'),
    (1006, 'Fajar Permata', 'fajar.permata@students.campus.id'),
    (1007, 'Gita Dewi', 'gita.dewi@students.campus.id'),
    (1008, 'Hendra Rahayu', 'hendra.rahayu@students.campus.id'),
    (1009, 'Intan Saputra', 'intan.saputra@students.campus.id'),
    (1010, 'Joko Wijaya', 'joko.wijaya@students.campus.id'),
    (1011, 'Kirana Hakim', 'kirana.hakim@students.campus.id'),
    (1012, 'Lukman Pratama', 'lukman.pratama@students.campus.id'),
    (1013, 'Maya Wulandari', 'maya.wulandari@students.campus.id'),
    (1014, 'Nanda Kusuma', 'nanda.kusuma@students.campus.id'),
    (1015, 'Oka Anggraini', 'oka.anggraini@students.campus.id'),
    (1016, 'Aditya Wulandari', 'aditya.wulandari@students.campus.id'),
    (1017, 'Bunga Kusuma', 'bunga.kusuma@students.campus.id'),
    (1018, 'Citra Anggraini', 'citra.anggraini@students.campus.id'),
    (1019, 'Dimas Lestari', 'dimas.lestari@students.campus.id'),
    (1020, 'Eka Nugroho', 'eka.nugroho@students.campus.id'),
    (1021, 'Fajar Santoso', 'fajar.santoso@students.campus.id'),
    (1022, 'Gita Putra', 'gita.putra@students.campus.id'),
    (1023, 'Hendra Maharani', 'hendra.maharani@students.campus.id'),
    (1024, 'Intan Permata', 'intan.permata@students.campus.id'),
    (1025, 'Joko Dewi', 'joko.dewi@students.campus.id'),
    (1026, 'Kirana Rahayu', 'kirana.rahayu@students.campus.id'),
    (1027, 'Lukman Saputra', 'lukman.saputra@students.campus.id'),
    (1028, 'Maya Wijaya', 'maya.wijaya@students.campus.id'),
    (1029, 'Nanda Hakim', 'nanda.hakim@students.campus.id'),
    (1030, 'Oka Pratama', 'oka.pratama@students.campus.id'),
    (1031, 'Aditya Wijaya', 'aditya.wijaya@students.campus.id'),
    (1032, 'Bunga Hakim', 'bunga.hakim@students.campus.id'),
    (1033, 'Citra Pratama', 'citra.pratama@students.campus.id'),
    (1034, 'Dimas Wulandari', 'dimas.wulandari@students.campus.id'),
    (1035, 'Eka Kusuma', 'eka.kusuma@students.campus.id'),
    (1036, 'Fajar Anggraini', 'fajar.anggraini@students.campus.id'),
    (1037, 'Gita Lestari', 'gita.lestari@students.campus.id'),
    (1038, 'Hendra Nugroho', 'hendra.nugroho@students.campus.id'),
    (1039, 'Intan Santoso', 'intan.santoso@students.campus.id'),
    (1040, 'Joko Putra', 'joko.putra@students.campus.id'),
    (1041, 'Kirana Maharani', 'kirana.maharani@students.campus.id'),
    (1042, 'Lukman Permata', 'lukman.permata@students.campus.id'),
    (1043, 'Maya Dewi', 'maya.dewi@students.campus.id'),
    (1044, 'Nanda Rahayu', 'nanda.rahayu@students.campus.id'),
    (1045, 'Oka Saputra', 'oka.saputra@students.campus.id');
