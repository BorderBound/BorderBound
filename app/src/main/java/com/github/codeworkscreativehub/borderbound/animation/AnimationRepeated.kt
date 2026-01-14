package com.github.codeworkscreativehub.borderbound.animation

class AnimationRepeated(animation: AnimationSingle) : Animation(animation.subject, 0) {
    private val animationForward: AnimationSingle = animation
    private val animationBackward: AnimationSingle
    private var isRunningForward = true
    private var shouldBeStopped = false

    init {
        animation.destroy() // Removes from subject array. Managed using this wrapper now.
        animation.firstTick() // To initialize "from"
        this.animationBackward = animation.reverse()
    }

    override fun tick(durationRunning: Long) {
        if (durationRunning > animationForward.duration) {
            if (shouldBeStopped && !isRunningForward) {
                this.destroy()
                return
            }

            isRunningForward = !isRunningForward
            animationForward.restart()
            animationBackward.restart()
            super.restart()
        }

        if (isRunningForward) {
            animationForward.tick()
        } else {
            animationBackward.tick()
        }
    }

    override fun start() {
        super.start()
        animationForward.restart()
        isRunningForward = true
        shouldBeStopped = false
    }

    override fun restart() {
        animationForward.restart()
        isRunningForward = true
    }

    fun stopWhenFinished() {
        shouldBeStopped = true
    }
}
