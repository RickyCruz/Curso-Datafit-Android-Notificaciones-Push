<?php

require_once 'config.php';

class Database
{
    private static $db = null;
    private static $pdo;

    final private function __construct()
    {
        try {
            self::getDb();
        } catch (PDOException $e) {
        }
    }

    public static function getInstance()
    {
        if (self::$db === null) {
            self::$db = new self();
        }

        return self::$db;
    }

    public function getDb()
    {
        if (self::$pdo == null) {
            self::$pdo = new PDO(
                'mysql:dbname=' . DATABASE .
                ';host=' . HOSTNAME .
                ';port:63343;', // Eliminar este elemento si se usa una instalación por defecto
                USERNAME,
                PASSWORD,
                array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8")
            );

            self::$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        }

        return self::$pdo;
    }

    final protected function __clone()
    {
    }

    function _destructor()
    {
        self::$pdo = null;
    }
}
