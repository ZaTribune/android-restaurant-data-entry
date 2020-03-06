package shadow.android.data_entry_1.about;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import shadow.android.data_entry_1.R;
import shadow.android.data_entry_1.client.ClientFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    private ClientFragment.TaskIsDoneInterface taskIsDoneInterface;
    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        taskIsDoneInterface= (ClientFragment.TaskIsDoneInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        taskIsDoneInterface.done();
        taskIsDoneInterface=null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about, container, false);
        TextView tv_toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.tv_toolbar);
        ImageButton btn_options = getActivity().findViewById(R.id.btn_options);

        TextView tv_twitter = view.findViewById(R.id.tv_twitter);

        tv_twitter.setOnClickListener(v -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://twitter.com/ZaTribune"));
            startActivity(intent);
        });

        tv_toolbar.setText(R.string.about_app);
        btn_options.setVisibility(View.GONE);
        return view;
    }

}
