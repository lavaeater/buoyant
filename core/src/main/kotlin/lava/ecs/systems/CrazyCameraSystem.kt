package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ktx.math.plus
import ktx.math.unaryMinus
import ktx.math.vec3
import twodee.ecs.ashley.components.TransformComponent
import twodee.ecs.ashley.systems.CameraFollowSystem

class CrazyCameraSystem(camera: OrthographicCamera, alpha: Float) : CameraFollowSystem(camera, alpha) {

    private val directionVector = Vector2.Y.cpy()
    private val cameraUp = vec3(0f, 0f, 0f)
        get() = field.set(directionVector.x, directionVector.y, 0f)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = TransformComponent.get(entity).position
        directionVector.setAngleDeg(TransformComponent.get(entity).angleDegrees)
        val reverseDirection = directionVector.cpy()
        cameraPosition.set(position + reverseDirection.scl(5f))

        camera.position.lerp(
            vec3(cameraPosition, 0f), alpha
        )
        camera.up.lerp(cameraUp, alpha)

        camera.update(true)
    }
}
