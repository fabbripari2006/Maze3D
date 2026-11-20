package it.unibs.pajc.maze.client;

import it.unibs.pajc.maze.model.Direction;

public record RayHit(double distance, Direction face, double wallOffset) {

}
