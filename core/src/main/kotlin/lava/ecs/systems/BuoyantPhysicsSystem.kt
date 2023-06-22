package lava.ecs.systems

import twodee.ecs.ashley.systems.Box2dUpdateSystem

class BuoyantPhysicsSystem(timeStep: Float, velIters:Int, posIters:Int):Box2dUpdateSystem(timeStep, velIters, posIters) {
    override fun everyTimeStep(deltaTime: Float) {

    }
}
