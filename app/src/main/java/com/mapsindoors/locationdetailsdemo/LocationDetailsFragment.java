package com.mapsindoors.locationdetailsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mapsindoors.R;
import com.mapsindoors.mapssdk.Location;
import com.mapsindoors.mapssdk.LocationPropertyNames;
import com.mapsindoors.mapssdk.MapControl;
import com.mapsindoors.mapssdk.MapsIndoors;


/***
 ---
 title: Show Location Details
 ---

 This is an example of displaying some details of a MapsIndoors location

 Start by creating a `Fragment or an Activity` class that contains the google map fragment
 ***/

public class LocationDetailsFragment extends Fragment {

    /***
     Add a `GoogleMap` and a `MapControl` to the class
     ***/
    MapControl mMapControl;
    GoogleMap mGoogleMap;

    /***
     Add other needed views for this example
     ***/

    SupportMapFragment mMapFragment;
    TextView detailsTextView;

    /***
     The lat lng of the Venue
     ***/
    static final LatLng VENUE_LAT_LNG = new LatLng( 57.05813067, 9.95058065 );
    //

    public LocationDetailsFragment()
    {
        // Required empty public constructor
    }

    public static LocationDetailsFragment newInstance()
    {
        return new LocationDetailsFragment();
    }


    //Region FRAGMENT LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_location_details, container, false);
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState )
    {
        super.onViewCreated( view, savedInstanceState );

        setupView( view );
    }

    @Override
    public void onDestroyView()
    {
        if( mMapControl != null )
        {
            mMapControl.onDestroy();
        }

        super.onDestroyView();
    }
    //endregion

    /***
     Setup the needed views for this example
     ***/
    private void setupView( View rootView )
    {
        FragmentManager fm = getChildFragmentManager();

        detailsTextView = rootView.findViewById( R.id.details_text_view );

        mMapFragment = (SupportMapFragment) fm.findFragmentById( R.id.mapfragment );

        mMapFragment.getMapAsync( mOnMapReadyCallback );
    }

    OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady( GoogleMap googleMap )
        {
            mGoogleMap = googleMap;
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( VENUE_LAT_LNG, 13.0f ) );

            setupMapsIndoors();
        }
    };

    /***
     Setup MapsIndoors
     ***/
    void setupMapsIndoors()
    {
        /***
         Setting the API key to the desired solution
         ***/
        if( !MapsIndoors.getAPIKey().equalsIgnoreCase( getString( R.string.mi_api_key ) ) )
        {
            MapsIndoors.setAPIKey( getString( R.string.mi_api_key ) );
        }

        if( getActivity() == null )
        {
            return;
        }

        mMapControl = new MapControl( getActivity(), mMapFragment, mGoogleMap );

        /***
         When a marker is clicked, get related MapsIndoors location object and set label text, based on the name and description of the location
         ***/
        mMapControl.setOnMarkerClickListener( marker -> {

            final Location loc = mMapControl.getLocation( marker );
            if( loc != null )
            {
                marker.showInfoWindow();

                if( detailsTextView.getVisibility() != View.VISIBLE )
                {
                    detailsTextView.setVisibility( View.VISIBLE );
                }

                /***
                 Show the Name and the description of a POI in a label
                 ***/
                detailsTextView.setText( "Name: " + loc.getName() + "\nDescription: " + loc.getStringProperty( LocationPropertyNames.DESCRIPTION ) );
            }

            return true;
        } );

        /***
         Init the MapControl object which will sync data
         ***/
        mMapControl.init( miError -> {

            if( miError == null )
            {
                Activity context = getActivity();
                if( context != null )
                {
                    context.runOnUiThread( () -> {
                        /***
                         Select a floor and animate the camera to the venue position
                         ***/
                        mMapControl.selectFloor( 1 );
                        mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( VENUE_LAT_LNG, 20f ) );
                    });
                }
            }
        });
    }
}
