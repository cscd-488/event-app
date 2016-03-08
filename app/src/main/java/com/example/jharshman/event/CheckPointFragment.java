/**
 * @file CheckPointFragment.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 *
 * Check Point Fragment used to display checkpoints
 * in a list and handle clicks on them.
 */

package com.example.jharshman.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CheckPointFragment extends Fragment {

    /**
     * Final Keys/Tags
     */
    private static final String TAG = "CheckPointFragment";
    public static final String CHECK_POINT_KEY = "check_point_key";

    /**
     * Singleton getInstance of fragment
     */
    private static CheckPointFragment mInstance;

    /**
     * Check point we are representing and handling in the fragment
     */
    private CheckPoint mCheckPoint;

    public CheckPointFragment() {
        // required empty constructor
    }

    /**
     * Factory method for creating or getting new singleton
     * getInstance of the fragment
     *
     * @param checkPoint The checkpoint to display
     *
     * @return A new getInstance of fragment CheckPointFragment.
     */
    public static CheckPointFragment newInstance(CheckPoint checkPoint) {

        // create check points fragment
        if(mInstance == null) {
             mInstance = new CheckPointFragment();
        }

        // set check point arguments on fragment
        Bundle args = new Bundle();
        args.putSerializable(CHECK_POINT_KEY, checkPoint);
        mInstance.setArguments(args);

        return mInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int eventID = -1;
        if (getArguments() != null) {
            // get Event ID
            mCheckPoint = (CheckPoint) getArguments().getSerializable(CHECK_POINT_KEY);
        }
        else {
            throw new NullPointerException("Check point must be set using Key CheckPointFragment.CHECK_POINT_KEY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_point, container, false);

        // get views
        ImageView image = (ImageView) view.findViewById(R.id.fragment_check_point_title_image);
        TextView title = (TextView) view.findViewById(R.id.fragment_check_point_title_text);
        TextView author = (TextView) view.findViewById(R.id.fragment_check_point_author_text);
        TextView description = (TextView) view.findViewById(R.id.fragment_check_point_description_text);

        // load data into views
        Picasso.with(getContext())
                .load(mCheckPoint.getImageSrc())
                .into(image);
        title.setText(mCheckPoint.getTitle());
        description.setText(mCheckPoint.getDescription());
//        description.setText("Non qui unde eum nostrum sunt sed quisquam dignissim pede aut parturient, iusto tempus nostrum, ea, dolor suspendisse, possimus occaecat, interdum non animi orci? Duis mus mollit vivamus quae aliquip! Saepe dapibus est ratione arcu, nisl. Quae eius odit eu, vitae cursus, laboris quidem, nostrum litora odio cum, habitant officia rem netus ante tortor, volutpat perferendis maxime, dignissim curabitur volutpat, orci risus maiores do amet aut, suscipit arcu necessitatibus, eros laborum inventore tempus dolores repellendus totam, placerat odio? Ea et! Do quas primis beatae tortor deleniti conubia egestas. Sunt laboris, placerat, nemo, animi conubia diam. Ducimus. Congue dolores sequi harum.\n" +
//                "\n" +
//                "Quisque distinctio. Sint. Porta, minus? Ratione nemo tempus hac iaculis neque nibh. Dignissimos unde vero venenatis praesent ab adipisci penatibus cumque, doloremque integer voluptatum incididunt excepturi? Sociosqu animi optio hymenaeos doloremque quam mollitia potenti porta lacinia, quo praesentium eligendi? Recusandae lacus mollit, cursus, minus temporibus? Perspiciatis ipsum ligula laoreet est dictum maecenas rhoncus dolore harum ea, adipisci facilis aenean sit? Ullamcorper numquam magnam nam sem mollit felis netus, deleniti itaque, natus ac. Aliquam et eveniet, lacus iure, aute, perspiciatis pharetra, labore? Odit, eius viverra mollitia nemo optio aptent, pariatur. Illum, velit assumenda phasellus elit? Corrupti eros mollis tenetur adipisci nisl.\n" +
//                "\n" +
//                "Suspendisse porta fuga praesent delectus tempora! Ex nullam. Ut nullam laudantium nam, tortor ullamcorper consectetuer occaecat! Posuere adipisicing, nam dis! Ullamco officia fusce perferendis massa, perferendis neque ullamco. Aliqua officia, illo sollicitudin accusantium rhoncus. Blandit urna! Repudiandae euismod lacus primis sociis culpa. Nostrud ullam cum? Etiam consequuntur sodales? Illum eaque? Accusantium gravida. Hac nesciunt. Aliqua est? Nisi aenean aliquet incidunt nihil itaque ultrices voluptate etiam tempus, lobortis eius saepe amet? Illo tincidunt tristique porttitor provident laborum pulvinar libero vivamus doloribus fringilla nisi, veniam iure eveniet natoque pellentesque convallis molestie adipiscing accusamus doloremque natus itaque, debitis pretium ducimus justo, sapien aenean.");
        return view;
    }
}
