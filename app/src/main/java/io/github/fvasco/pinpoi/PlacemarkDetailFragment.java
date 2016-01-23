package io.github.fvasco.pinpoi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import io.github.fvasco.pinpoi.dao.PlacemarkCollectionDao;
import io.github.fvasco.pinpoi.dao.PlacemarkDao;
import io.github.fvasco.pinpoi.model.Placemark;
import io.github.fvasco.pinpoi.model.PlacemarkAnnotation;
import io.github.fvasco.pinpoi.model.PlacemarkCollection;
import io.github.fvasco.pinpoi.util.Util;

/**
 * A fragment representing a single Placemark detail screen.
 * This fragment is either contained in a {@link PlacemarkListActivity}
 * in two-pane mode (on tablets) or a {@link PlacemarkDetailActivity}
 * on handsets.
 */
public class PlacemarkDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private EditText noteText;

    private Placemark placemark;
    private PlacemarkCollection placemarkCollection;
    private PlacemarkDao placemarkDao;
    private PlacemarkCollectionDao placemarkCollectionDao;
    private PlacemarkAnnotation placemarkAnnotation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlacemarkDetailFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placemarkDao = PlacemarkDao.getInstance().open();
        placemarkCollectionDao = PlacemarkCollectionDao.getInstance().open();

        final SharedPreferences preferences = getActivity().getSharedPreferences(PlacemarkDetailFragment.class.getSimpleName(), Context.MODE_PRIVATE);
        final long id = getArguments().getLong(ARG_ITEM_ID, preferences.getLong(ARG_ITEM_ID, 0));
        Log.i(PlacemarkDetailFragment.class.getSimpleName(), "open placemark " + id);
        placemark = placemarkDao.getPlacemark(id);
        assert placemark != null;
        placemarkCollection = placemarkCollectionDao.findPlacemarkCollectionById(placemark.getCollectionId());
        placemarkAnnotation = placemarkDao.loadPlacemarkAnnotation(placemark);
        preferences.edit().putLong(ARG_ITEM_ID, placemark.getId()).apply();

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(placemark.getName());
        }
    }

    @Override
    public void onDestroy() {
        placemarkAnnotation.setNote(noteText.getText().toString());
        placemarkDao.update(placemarkAnnotation);
        placemarkDao.close();
        placemarkCollectionDao.close();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.placemark_detail, container, false);
        noteText = ((EditText) rootView.findViewById(R.id.note));
        ((TextView) rootView.findViewById(R.id.placemark_detail))
                .setText(placemark.getDescription() == null
                        ? placemark.getName()
                        : placemark.getName() + "\n\n" + placemark.getDescription());
        ((TextView) rootView.findViewById(R.id.coordinate)).setText(Util.formatCoordinate(placemark));
        noteText.setText(placemarkAnnotation.getNote());
        if (placemarkCollection != null) {
            ((TextView) rootView.findViewById(R.id.placemark_collection_description)).setText(placemarkCollection.getDescription());
        }
        return rootView;
    }

    public PlacemarkAnnotation getPlacemarkAnnotation() {
        return placemarkAnnotation;
    }
}