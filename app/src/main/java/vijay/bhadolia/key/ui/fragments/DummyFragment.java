package vijay.bhadolia.key.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vijay.bhadolia.key.Interfaces.PasswordClickListener;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.adapter.PasswordAdapter;
import vijay.bhadolia.key.model.Password;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DummyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DummyFragment extends Fragment {

    private static final String TAG = DummyFragment.class.getName();

    //Variables for recyclerView
    private RecyclerView mRecyclerView;
    private PasswordAdapter mAdapter;
    private List<Password> passwordList;

    private static int itemPosition = -1;
    private static Password deletedPassword;

    private TextView textView;

    public DummyFragment() {        // Required empty public constructor
    }

    public static DummyFragment newInstance() {
        DummyFragment fragment = new DummyFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //main
        initViews(view);
        populateFakePasswordList();
        buildRecyclerView();
    }

    private void initViews(View view) {
        textView = view.findViewById(R.id.textView);
        mRecyclerView = view.findViewById(R.id.recyclerView);
    }

    private void buildRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mAdapter = new PasswordAdapter(getContext(), new PasswordClickListener() {
            @Override
            public void onClick(int position) {

            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.submitList(passwordList);
        registerForContextMenu(mRecyclerView);

        //Interface Methods
    }

    private void populateFakePasswordList() {
        passwordList = new ArrayList<>();

        Password password_0 = new Password("Gmail", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_1 = new Password("Facebook", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_2 = new Password("twitter", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_3 = new Password("Google+", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_4 = new Password("Instagram", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_5 = new Password("Vimeo", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_6 = new Password("MindGeek", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_7 = new Password("HelloWorld", "heyItMyGmail.com", "vijayfiosdhgsd", "");
        Password password_8 = new Password("StackOverFlow", "heyItMyGmail.com", "vijayfiosdhgsd", "");

        passwordList.add(password_0);
        passwordList.add(password_1);
        passwordList.add(password_2);
        passwordList.add(password_3);
        passwordList.add(password_4);
        passwordList.add(password_5);
        passwordList.add(password_6);
        passwordList.add(password_7);
        passwordList.add(password_8);
    }
}