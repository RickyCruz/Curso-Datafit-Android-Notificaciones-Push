<?php

require './config/database.php';;

class User
{

    function __construct()
    {
    }

    public static function storeUser($name, $email, $gcm_regid)
    {
        $query  = "INSERT INTO gcm_users($name, email, gcm_regid, created_at) VALUES (?,?,?,NOW())";
        $cmd    = Database::getInstance()->getDb()->prepare($query);

        $result = $cmd->execute([$name, $email, $gcm_regid]);

        if ($result) {

            $id = Database::getInstance()->getDb()->lastInsertId();

            $query = "SELECT * FROM gcm_users WHERE id = ?";

            try {
                $cmd = Database::getInstance()->getDb()->prepare($query);
                $cmd->execute([$id]);
                $row = $cmd->fetch(PDO::FETCH_ASSOC);

                return ($cmd->rowCount() > 0) ? $row : false;
            } catch (PDOException $e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static function getAllUsers()
    {
        $query = "SELECT * FROM gcm_users";

        try {
            $cmd = Database::getInstance()->getDb()->prepare($query);
            $cmd->execute();

            return $cmd->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            return [];
        }
    }
}