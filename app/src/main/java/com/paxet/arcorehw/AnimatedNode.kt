package com.paxet.arcorehw

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator

class AnimatedNode : Node() {
    // We'll use Property Animation to make this node rotate.

    private var rotationAnimation: ObjectAnimator? = null
    private var translationAnimation: ObjectAnimator? = null
    private var degreesPerSecond = 90.0f

    private var lastSpeedMultiplier = 1.0f

    private val animationDuration: Long
        get() = (1000 * 360 / (degreesPerSecond * speedMultiplier)).toLong()

    private val speedMultiplier: Float
        get() = 1.0f

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)

        // Animation hasn't been set up.
        if (rotationAnimation == null) {
            return
        }

        // Check if we need to change the speed of rotation.
        val speedMultiplier = speedMultiplier

        // Nothing has changed. Continue rotating at the same speed.
        if (lastSpeedMultiplier == speedMultiplier) {
            return
        }

        if (speedMultiplier == 0.0f) {
            rotationAnimation!!.pause()
        } else {
            rotationAnimation!!.resume()

            val animatedFraction = rotationAnimation!!.animatedFraction
            rotationAnimation!!.duration = animationDuration
            rotationAnimation!!.setCurrentFraction(animatedFraction)
        }
        lastSpeedMultiplier = speedMultiplier
    }

    /** Sets rotation speed  */
    fun setDegreesPerSecond(degreesPerSecond: Float) {
        this.degreesPerSecond = degreesPerSecond
    }

    override fun onActivate() {
        startAnimation()
    }

    override fun onDeactivate() {
        stopAnimation()
    }

    private fun startAnimation() {
        if (rotationAnimation != null) {
            return
        }
        val animationSet = AnimatorSet()

        rotationAnimation = createRotationAnimator()
        rotationAnimation!!.target = this
        rotationAnimation!!.duration = animationDuration

        translationAnimation = createTranslationAnimator()
        translationAnimation!!.target = this
        translationAnimation!!.duration = animationDuration

        animationSet.play(rotationAnimation).with(translationAnimation)

        animationSet.start()

    }

    private fun stopAnimation() {
        if (rotationAnimation == null) {
            return
        }
        rotationAnimation!!.cancel()
        rotationAnimation = null
    }

    /** Returns an ObjectAnimator that makes this node rotate.  */
    private fun createRotationAnimator(): ObjectAnimator {
        // Node's setLocalRotation method accepts Quaternions as parameters.
        // First, set up orientations that will animate a circle.
        val orientation1 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f)
        val orientation2 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 120f)
        val orientation3 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 240f)
        val orientation4 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 360f)

        val rotationAnimation = ObjectAnimator()
        rotationAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4)

        // Next, give it the localRotation property.
        rotationAnimation.setPropertyName("localRotation")

        // Use Sceneform's QuaternionEvaluator.
        rotationAnimation.setEvaluator(QuaternionEvaluator())

        //  Allow rotationAnimation to repeat forever
        rotationAnimation.repeatCount = ObjectAnimator.INFINITE
        rotationAnimation.repeatMode = ObjectAnimator.RESTART
        rotationAnimation.interpolator = LinearInterpolator()
        rotationAnimation.setAutoCancel(true)

        return rotationAnimation
    }

    /** Returns an ObjectAnimator that makes this node rotate.  */
    private fun createTranslationAnimator(): ObjectAnimator {

        val translationAnimation = ObjectAnimator()

        val vector1 = Vector3(-0.2f, this.localPosition.y, this.localPosition.z)
        val vector2 = Vector3(this.localPosition.x, this.localPosition.y, 0.2f)
        val vector3 = Vector3(0.2f, this.localPosition.y, this.localPosition.z)
        val vector4 = Vector3(this.localPosition.x, this.localPosition.y, -0.2f)
        val vector5 = Vector3(-0.2f, this.localPosition.y, this.localPosition.z)
        translationAnimation.setObjectValues(vector1, vector2, vector3, vector4, vector5)

        translationAnimation.setPropertyName("localPosition")
        translationAnimation.setEvaluator(Vector3Evaluator())

        //  Allow translationAnimation to repeat forever
        translationAnimation.repeatCount = ObjectAnimator.INFINITE
        translationAnimation.repeatMode = ObjectAnimator.RESTART

        translationAnimation.interpolator = LinearInterpolator()
        translationAnimation.setAutoCancel(true)


        return translationAnimation
    }
}