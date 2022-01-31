
package org.disrupted.ibits.userinterface.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.disrupted.ibits.R;

/**
 * @author
 */
public class FragmentFavoriteList extends Fragment {

    private static View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        return mView;
    }
}
