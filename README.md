<img align="right" src="https://raw.githubusercontent.com/PFGimenez/The-Kraken-Pathfinding/master/resources/logo.png">

# The Kraken Pathfinding

A tentacle-based pathfinding library for nonholonomic robotic vehicles 

## What is Kraken ?

Kraken finds a trajectory followable by a car-like vehicle in the form of a list of points.

### Features

The trajectory created by Kraken has several geometric properties, making it suitable for robotic vehicles, namely:

- position continuity (G0 continuity) ;
- orientation continuity (G1 continuity) ;
- curvature piecewise continuity (piecewise G2 continuity) ;
- handles forward and backward movement.

Currently, Kraken finds a trajectory between from a position and an orientation to a position. Which means that, for example, you can't force the orientation at the end point. A next version will expand the possibilities.

![Trajectory example](https://raw.githubusercontent.com/PFGimenez/The-Kraken-Pathfinding/master/resources/example.png)

### Roadmap

A roadmap is available in the wiki of the project: https://github.com/PFGimenez/The-Kraken-Pathfinding/wiki/Roadmap

### Why Java ?

For legacy reasons, mainly.

## Getting Kraken

### Downloading the last stable version

You can download the .jar file here: https://github.com/PFGimenez/The-Kraken-Pathfinding/releases/download/v1.0.1/kraken.jar

### Getting the source [![Build Status](https://travis-ci.org/PFGimenez/The-Kraken-Pathfinding.svg?branch=master)](https://travis-ci.org/PFGimenez/The-Kraken-Pathfinding)

If you want the latest stable version, clone this repository:

    $ git clone https://github.com/PFGimenez/The-Kraken-Pathfinding.git

If you want the latest **experimental** version, clone this repository:

    $ git clone -b v1.1 --single-branch https://github.com/PFGimenez/The-Kraken-Pathfinding.git


### Compiling

You will need a JDK and `ant` (package `ant` or `apache-ant`):
    
    $ cd The-Kraken-Pathfinding/core
    $ ./compile-lib.sh
    $ ant

The file ```kraken.jar```, containing the compiled code .class and the sources .java along with the dependencies .jar, will be created.

Examples are available in the directory ```examples```.

### Unit testing

You can easily run the tests:

    $ cd The-Kraken-Pathfinding/tests
    $ ant

## Great, I have a trajectory. How do I follow it ?

Getting the trajectory is only half of the work, because you won't go far if your robot can't follow it. Different control algorithms exist; in this section the Samson control algorithm [1] is presented.

First, recall that the [curvature](https://en.wikipedia.org/wiki/Curvature#Curvature_of_plane_curves) of a curve _C_ at the point _P_ is the inverse of the radius of curvature at _P_, i.e. the inverse of the radius of the circle that "fits" the best _C_ at _P_.

<img align="right" src="https://raw.githubusercontent.com/PFGimenez/The-Kraken-Pathfinding/master/resources/asser-samson.png">

Let _R_ be the vehicle and _R'_ its orthogonal projection to the curve and denote _θ(R)_ the orientation of the robot, _θ(R')_ the orientation setpoint at _R'_, _κ(R')_ the curvature setpoint at _R'_ and _d_ the algebric distance between _R_ and _R'_ (_d > 0_ if the robot is on the left of the curve, _d < 0_ otherwise).

The curvature setpoint is _κ = κ(R') - k₁×d - k₂×(θ(R) - θ(R'))_, where _k₁_ and _k₂_ are two constants depending on the system.

In practice, if the robot has a small orientation error, one can approximate _d_ with _(R.y - R'.y) cos(θ(R')) - (R.x - R'.x) sin(θ(R'))_.

This control algorithm has been successfully used with Kraken in the [INTech Senpaï Moon-Rover project (french !)](https://intechsenpai.github.io/moon-rover/).

## Contributing

### How to help ?

Feel free to contribute to Kraken ! There are two easy ways of helping me:

- submit an issue containing your ideas ;
- create a pull request.

### Bug report

Bug report are done with issues. Please be careful to respect those few points:

- check if the bug hasn't be reported yet ;
- indicate the version of Kraken you use ;
- describe your problem as best as possible. If you can post a minimal code that triggers the bug, it will be much easier for me ;
- attach any relevant file.

## Bibliography

[1] Samson, C. (1995). Control of chained systems application to path following and time-varying point-stabilization of mobile robots. IEEE transactions on Automatic Control, 40(1), 64-77.
