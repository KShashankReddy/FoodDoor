package com.example.shashankreddy.fooddoor.fragments;



import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shashankreddy.fooddoor.R;
import com.example.shashankreddy.fooddoor.appController.AppController;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressMapFragment extends Fragment implements OnMapReadyCallback {
    View mview;
    MapView mMapView;
    GoogleMap gMap;
    private EditText mEditText;
    private TextView mDeliveryLocation,mDeliveryLocation2;
    private CardView mConfirmCardView;
    public static final String FRAGMENTTAG = "fragmentTag";
    String mTag;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private boolean mLocationPermissionGranted;

    double mLongitude,mLatitude;
    private LatLng mLatLng;
    Address mDeliveryAddress;
    public AddressMapFragment() {
        // Required empty public constructor
    }

    public static AddressMapFragment newInstance(String mTag){
        AddressMapFragment fragment = new AddressMapFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENTTAG,mTag);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTag = getArguments().getString(FRAGMENTTAG);
        Log.d(AddressMapFragment.class.getSimpleName(), mLatitude + " 55555 " + mLongitude);
            mLatitude = AppController.getInstance().getmUserAddress().getmLatitude();
            mLongitude = AppController.getInstance().getmUserAddress().getmLogitude();
            Log.d(AddressMapFragment.class.getSimpleName(),mLatitude+" 55555 "+mLongitude);
        if(mLongitude!= 0 && mLatitude!=0) {
            mLatLng = new LatLng(mLatitude,mLongitude);
            Log.d(AddressMapFragment.class.getSimpleName(),mLatLng.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_address_map, container, false);
        mEditText = (EditText) mview.findViewById(R.id.map_userAddressSet);
        ImageView mImageView = (ImageView) mview.findViewById(R.id.search_imageView);
        TextView currentLoction = (TextView) mview.findViewById(R.id.currentUserLocation);
        mDeliveryLocation = (TextView) mview.findViewById(R.id.delivery_user_location);
        mDeliveryLocation2 = (TextView) mview.findViewById(R.id.delivery_user_location_address2);
        mConfirmCardView = (CardView) mview.findViewById(R.id.address_conformation_cardview);
        CardView mCurrentLocationAddres = (CardView) mview.findViewById(R.id.current_location_address);
        mDeliveryAddress = AppController.getInstance().getmUserAddress().getCurrentAddressObj();
        mDeliveryLocation.setText( mDeliveryAddress.getAddressLine(0));
        mDeliveryLocation2.setText( mDeliveryAddress.getLocality()+" "+ mDeliveryAddress.getCountryCode()+" "+ mDeliveryAddress.getPostalCode());
        currentLoction.setText(AppController.getInstance().getmUserAddress().getAddress1());
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locaTion = mEditText.getText().toString();
                if (locaTion != null && !locaTion.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());

                    try {
                        List<Address> addressList = geocoder.getFromLocationName(locaTion, 2);
                        mDeliveryAddress = addressList.get(0);
                        LatLng latLng = new LatLng(mDeliveryAddress.getLatitude(), mDeliveryAddress.getLongitude());
                        updateMarkers(latLng);
                        mDeliveryLocation.setText(mDeliveryAddress.getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mCurrentLocationAddres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeliveryAddress = AppController.getInstance().getmUserAddress().getCurrentAddressObj();
            }
        });
        mConfirmCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().getmUserAddress().setDeliveryAddress(mDeliveryAddress);
                if(mTag.equals("HomeFragment")){
                 getFragmentManager().beginTransaction().replace(R.id.fragment_replaceable,new HomeFragment()).commit();
                }else {
                    Fragment fragment = getFragmentManager().findFragmentByTag(mTag);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_replaceable, fragment).commit();
                }
            }
        });
        return mview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mview.findViewById(R.id.map_fragment);
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        gMap = googleMap;
        setLocation();
        updateMarkers(mLatLng);
        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.map_content_info, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

    }

    private void updateMarkers(LatLng latLng) {
        Log.d(AddressMapFragment.class.getSimpleName(), latLng.toString());
        gMap.addMarker(new MarkerOptions().position(latLng).
                title("currentLocation")
                .snippet(AppController.getInstance().getmUserAddress().getAddress1()));
        gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

        mDeliveryLocation.setText(mDeliveryAddress.getAddressLine(0));
        mDeliveryLocation2.setText( mDeliveryAddress.getSubLocality()+" "+ mDeliveryAddress.getCountryCode()+" "+ mDeliveryAddress.getPostalCode());
    }

    private void setLocation() {
        if (gMap == null)
            return;

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        else {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }
}
