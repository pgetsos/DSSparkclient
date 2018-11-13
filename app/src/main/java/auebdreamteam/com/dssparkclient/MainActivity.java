/*
 * Copyright (c) 2018.
 * Created for MSc in Computer Science - Distributed Systems
 * All right reserved except otherwise noted
 */

package auebdreamteam.com.dssparkclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import auebdreamteam.com.dssparkclient.databinding.ActivityMainBinding;
import auebdreamteam.com.dssparkclient.helpers.FixAppBarLayoutBehavior;
import auebdreamteam.com.dssparkclient.ui.MapFragment;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Drawer result;
    private List<Fragment> pages;
    private int lastPosition = 1;
    private int drawerItemIdentifier = 1;
    private int drawerColor = R.color.material_drawer_background;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Hawk.init(this).build();

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);
        //binding.collapsingToolbar.setTitle(getString(R.string.toolbar_title));
        ((CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams()).setBehavior(new FixAppBarLayoutBehavior());

		Fragment map = new MapFragment();
		getFragmentManager().beginTransaction()
				.add(R.id.fragment, map, "map")
				//.addToBackStack(null)
				.commit();

		openCoordinatesDialog();
        //binding.fab.setOnClickListener(view -> startActivity(new Intent(this, CreateVehicle.class)));
        initDrawer(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (Hawk.get(AboutPrefs.KEY_CHANGE, false)) {
            Hawk.put(AboutPrefs.KEY_CHANGE, false);
            for (Fragment page : pages) {
                getSupportFragmentManager().beginTransaction().remove(page).commitNow();
            }
            this.recreate();
        }*/
    }

    @Override
    public void onBackPressed() {
        if(result.isDrawerOpen()){
            result.closeDrawer();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Snackbar.make(findViewById(android.R.id.content), R.string.exit_text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    private void initDrawer(Bundle savedInstanceState){
       result = new DrawerBuilder(this)
                .withToolbar(binding.toolbar)
                .withDisplayBelowStatusBar(true)
                .withSliderBackgroundColorRes(drawerColor)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(R.drawable.common_google_signin_btn_icon_dark).withIdentifier(1).withIconTintingEnabled(true),
                        //new SectionDrawerItem().withDivider(false).withName(R.string.drawer_item_categories),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_line).withIcon(R.drawable.common_google_signin_btn_icon_dark).withIdentifier(2).withIconTintingEnabled(true)
                )
                .withShowDrawerOnFirstLaunch(!BuildConfig.DEBUG)
                .withSelectedItem(1)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    for (Fragment page : pages) {
                        getFragmentManager().beginTransaction().remove(page).commit();
                    }
                    drawerItemIdentifier = (int)drawerItem.getIdentifier();
                    switch (drawerItemIdentifier){
                        case 1:
                        	Fragment map = new MapFragment();
                        	pages.add(map);
                            getFragmentManager().beginTransaction()
									.add(R.id.fragment, map, "map")
									.addToBackStack(null)
									.commit();
                            break;
                        case 7:
                            result.setSelection(lastPosition);
//                            Intent i = new Intent(this, AboutPageActivity.class);
//                            startActivity(i);
                            return true;
                        default :
                            //binding.fab.setVisibility(View.GONE);
							Fragment map1 = new MapFragment();
							getFragmentManager().beginTransaction()
									.add(R.id.fragment, map1, "map")
									.addToBackStack(null)
									.commit();
                    }
                    lastPosition = drawerItemIdentifier;

                    result.closeDrawer();
                    return true;
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private void openCoordinatesDialog() {
		new MaterialDialog.Builder(this)
						.title(R.string.coordinates_starting_lat)
						.customView(R.layout.coordinates_dialog, true)
						.positiveText(R.string.ok_button)
						.negativeText(R.string.cancel_button)
						//.onPositive(
								//(dialog1, which) -> ())
						.build().show();
	}
}
