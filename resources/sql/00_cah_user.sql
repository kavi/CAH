CREATE DATABASE cah;
CREATE USER 'cah'@'localhost' IDENTIFIED BY 'cah_rw_123';
GRANT ALL ON cah.* TO 'cah'@'localhost';
FLUSH PRIVILEGES;