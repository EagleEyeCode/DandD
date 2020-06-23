package de.eagleeye.dandd.fragments.show;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseDataShowFragment;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class MonstersModelShowFragment extends BaseDataShowFragment {
    private ArFragment arFragment;
    private ModelRenderable modelRenderable;

    private File file;
    private float scale;
    private float rotateX;
    private float rotateY;
    private float rotateZ;

    private AnchorNode anchorNode;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        file = new File("");
        scale = 1.0f;
        rotateX = 0.0f;
        rotateY = 0.0f;
        rotateZ = 0.0f;

        Bundle args = getArguments();
        if(args != null) {
            BasicSQLiteHelper db = new BasicSQLiteHelper(getActivity(), "data.db");
            db.query(new SQLRequest("SELECT files.path, monsters.modelScale, monsters.modelRotateX, monsters.modelRotateY, monsters.modelRotateZ FROM monsters LEFT JOIN files ON monsters.modelId=files.id AND monsters.modelSourceId=files.sourceId WHERE monsters.id=" + args.get("id") + " AND monsters.sourceId=" + args.get("sourceId") + ";",
                    cursor -> {
                        if (cursor != null && getActivity() != null && cursor.moveToFirst()){
                            file = new File(getActivity().getFileStreamPath(""), cursor.getString(0));
                            scale = cursor.getFloat(1);
                            rotateX = cursor.getFloat(2);
                            rotateY = cursor.getFloat(3);
                            rotateZ = cursor.getFloat(4);
                        }
                    }), true);
        }

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
        if(getActivity() != null) {
            ModelRenderable.builder().setSource(getActivity(), RenderableSource.builder().setSource(
                    getActivity(),
                    Uri.fromFile(file),
                    RenderableSource.SourceType.GLB)
                    .setScale(scale)
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
    }

    private void setupPlane(){
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if(anchorNode != null && anchorNode.getAnchor() != null){
                arFragment.getArSceneView().getScene().removeChild(anchorNode);
                anchorNode.getAnchor().detach();
                anchorNode.setParent(null);
                anchorNode = null;
            }

            Anchor anchor = hitResult.createAnchor();
            anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            createModel(anchorNode);
        });
    }

    private void createModel(AnchorNode anchorNode) {
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setLocalRotation(Quaternion.axisAngle(new Vector3(rotateX, rotateY, rotateZ), 1f));
        node.setParent(anchorNode);
        node.setRenderable(modelRenderable);
        node.select();
    }
}
