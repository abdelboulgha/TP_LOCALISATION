<?php
include_once '../dao/IDao.php';
include_once '../classes/Position.php';
include_once '../connection/Connection.php';

class PositionService implements IDao
{
    private $listPosition = array();
    private $connexion;
    private $position;

    public function __construct()
    {
        $this->connexion = new Connexion();
        $this->position = new Position("", "", "", "", "");

    }

    public function create($position)
    {
        $query = "INSERT INTO position (latitude, longitude, date, imei) VALUES ("
            . $position->getLatitude() . "," . $position->getLongitude() . ",'" . $position->getDate() . "','" . $position->getImei() . "')";
        $req = $this->connexion->getConnextion()->prepare($query);
        $req->execute() or die('SQL');
    }

    public function delete($obj)
    {

    }

    public function getAll()
    {
        $query = "SELECT * FROM position";
        $req = $this->connexion->getConnextion()->prepare($query);
        $req->execute();
        $result = $req->fetchAll(PDO::FETCH_ASSOC);

        $positions = array();
        foreach ($result as $row) {
            $positions[] = new Position(
                $row['id'],
                $row['latitude'],
                $row['longitude'],
                $row['date'],
                $row['imei']
            );
        }

        return $positions;
    }

    public function getById($obj)
    {

    }

    public function update($obj)
    {

    }
}
