package com.paxet.arcorehw

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment


class MainActivity : AppCompatActivity() {

    var arFragment: ArFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if(Sceneform.isSupported(this)) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.ar_fragment, ArFragment::class.java, null, "ar_fragment")
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        arFragment = supportFragmentManager.findFragmentByTag("ar_fragment") as ArFragment
        arFragment?.arSceneView?.scene?.addOnUpdateListener(updateListener)
    }

    private val updateListener = Scene.OnUpdateListener {
        val frame = arFragment?.arSceneView?.arFrame
        frame?.let {
            for(plane in frame.getUpdatedTrackables(Plane::class.java)) {
                addObjectModel(Uri.parse("monkey.glb"))
                break
            }
        }
    }

    private fun addObjectModel(uri: Uri?) {
        val frame = arFragment?.arSceneView?.arFrame
        val center: Point = getScreenCenter()
        val hitResult = frame?.hitTest(center.x.toFloat(), center.y.toFloat())
        if (hitResult != null) {
            for(hit in hitResult) {
                val trackable = hit.trackable
                if(trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(hit.createAnchor(), uri)
                    arFragment?.arSceneView?.scene?.removeOnUpdateListener(updateListener)
                    break
                }
            }
        }
    }

    private fun placeObject(anchor: Anchor?, uri: Uri?) {
        ModelRenderable.builder()
            .setSource(arFragment?.context, uri)
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { model -> addNodeToScene(anchor, model, uri)}
            .exceptionally { throwable -> null }
    }

    private fun addNodeToScene(anchor: Anchor?, model: ModelRenderable, uri: Uri?) {
        val anchorNode = AnchorNode(anchor)
        val rotatingNode = AnimatedNode()
        rotatingNode.renderable = model
        rotatingNode.setParent(anchorNode)
        arFragment?.arSceneView?.scene?.addChild(anchorNode)
    }

    private fun getScreenCenter() : Point {
        if (arFragment == null || arFragment?.getView() == null) {
            return Point(0, 0)
        }

        val w: Int = arFragment?.getView()?.getWidth()?.div(2) ?:0
        val h: Int = arFragment?.getView()?.getHeight()?.div(2) ?:0
        return Point(w, h)
    }
}