package de.eagleeye.dandd.fragments.show;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;

public class MonstersModelShowFragment extends BaseDataShowFragment {
    private ArFragment arFragment;
    private ModelRenderable modelRenderable;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() != null) {
            arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.fragment_show_monster_model_fragment);
            setupModel();
            setupPlane();
        }
    }

    @Override
    protected String tableName() {
        return "monsters";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_show_monster_model;
    }

    private void setupModel(){
        ModelRenderable.builder().setSource(getActivity(), RenderableSource.builder().setSource(
                getActivity(),
                Uri.fromFile(new File(getActivity().getFileStreamPath("models"), "Dragon.glb")),
                RenderableSource.SourceType.GLB)
                .setScale(0.01f)  // Scale the original model to 50%.
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build())
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                })
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(getActivity(), "Unable to load renderable ", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            Log.e(MonstersModelShowFragment.this.getClass().getSimpleName(), "Unable to render Model", throwable);
                            return null;
                        });

    }

    private void setupPlane(){
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            createModel(anchorNode);
        });
    }

    private void createModel(AnchorNode anchorNode) {
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(modelRenderable);
        node.select();
    }
}
