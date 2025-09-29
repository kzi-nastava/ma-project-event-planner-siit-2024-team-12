package com.example.eventplanner.activities.homepage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.auth.LoginFragment;
import com.example.eventplanner.fragments.auth.SignUpFragment;
import com.example.eventplanner.fragments.business.BusinessInfoFragment;
import com.example.eventplanner.fragments.business.businessregistration.BusinessRegistrationFragment;
import com.example.eventplanner.enumeration.UserRole;
import com.example.eventplanner.fragments.calendar.CalendarFragment;
import com.example.eventplanner.fragments.conversation.ConversationListFragment;
import com.example.eventplanner.fragments.charts.AttendanceChartFragment;
import com.example.eventplanner.fragments.charts.RatingsChartFragment;
import com.example.eventplanner.fragments.conversation.ConversationWebSocketService;
import com.example.eventplanner.fragments.event.eventcreation.EventCreationFragment;
import com.example.eventplanner.fragments.eventtype.EventTypeTableFragment;
import com.example.eventplanner.fragments.favorites.ExplorePageFragment;
import com.example.eventplanner.fragments.favorites.FavoriteEventsFragment;
import com.example.eventplanner.fragments.favorites.FavoriteServicesFragment;
import com.example.eventplanner.fragments.profile.ProfileViewFragment;
import com.example.eventplanner.fragments.solutioncategory.CategoriesTableFragment;
import com.example.eventplanner.fragments.categories.CategoriesContainerFragment;
import com.example.eventplanner.fragments.comment.CommentManagementFragment;
import com.example.eventplanner.fragments.event.InvitedEventsListFragment;
import com.example.eventplanner.fragments.favorites.FavoriteProductsFragment;
import com.example.eventplanner.fragments.homepage.HomepageFragment;
import com.example.eventplanner.fragments.notification.NotificationFragment;
import com.example.eventplanner.fragments.notification.NotificationWebSocketService;
import com.example.eventplanner.fragments.product.ProvidedProductsFragment;
import com.example.eventplanner.fragments.profile.SuspendedUserFragment;
import com.example.eventplanner.fragments.profile.ViewUserProfileFragment;
import com.example.eventplanner.fragments.report.ReportManagementFragment;
import com.example.eventplanner.fragments.servicecreation.ServiceManagement;
import com.example.eventplanner.utils.ClientUtils;
import com.example.eventplanner.fragments.servicereservation.ServiceReservationsManagementFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class HomepageActivity extends AppCompatActivity implements NotificationWebSocketService.NotificationCountListener, ConversationWebSocketService.ConversationCountListener {

    private DrawerLayout drawerLayout;
    private RecyclerView chatRecyclerView;
    private NavigationView navigationView;
    private static final String TAG = "HomepageDebug";

    private NotificationWebSocketService notificationService;

    private TextView notificationBadge;

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerArrowDrawable originalDrawerIcon;
    private boolean notificationsOpen = false;

    private TextView messageBadge;

    private ConversationWebSocketService conversationWebSocketService;

    private boolean conversationOpen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Log.d(TAG, "onCreate: HomepageActivity created.");

        drawerLayout = findViewById(R.id.navigationView);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(null);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        originalDrawerIcon = toggle.getDrawerArrowDrawable();

        navigationView = findViewById(R.id.nav_view);

        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isSuspended = sp.getBoolean("isSuspended", false);

        String token = ClientUtils.getAuthorization(getApplicationContext());
        Log.d("TOKEN_CHECK", "Token: " + token);
        messageBadge = findViewById(R.id.message_badge_text);

        if (token != null && !isSuspended) {
            notificationService = new NotificationWebSocketService();
            notificationService.connect(token);
            notificationService.addListener(this);

            conversationWebSocketService = new ConversationWebSocketService();
            conversationWebSocketService.connect(token);
            conversationWebSocketService.addListener(this);

        }


        if (savedInstanceState == null) {
            if (isSuspended) {
                setupGuestUI();
                loadSuspendedFragment();
            } else {
                String role = sp.getString("userRole", UserRole.ROLE_UNREGISTERED_USER.toString());

                if ("ROLE_ORGANIZER".equals(role)) {
                    setupOrganizerUI();
                    enableChatDrawer();
                } else if ("ROLE_PROVIDER".equals(role)) {
                    setupProviderUI();
                    enableChatDrawer();
                } else if ("ROLE_ADMIN".equals(role)) {
                    setupAdminUI();
                } else if ("ROLE_AUTHENTICATED_USER".equals(role)) {
                    setupAuthenticatedUserUI();
                    enableChatDrawer();
                } else {
                    setupGuestUI();
                }
                loadMainFragment(new HomepageFragment());
            }
        }

        if (getIntent().getBooleanExtra("showLogin", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (sp.getBoolean("isSuspended", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("isSuspended");
            editor.apply();
        }
    }

    private void updateNotificationsBadge(int count) {
        updateHamburgerIcon(count > 0);

        if (notificationBadge != null && !notificationsOpen) {
            if (count > 0) {
                notificationBadge.setText(String.valueOf(count));
                notificationBadge.setVisibility(View.VISIBLE);
            } else {
                notificationBadge.setVisibility(View.GONE);
            }
        }
    }

    private void updateMessageBadge(int count) {

        if (messageBadge != null && !conversationOpen ) {
            if (count > 0) {
                messageBadge.setText(String.valueOf(count));
                messageBadge.setVisibility(View.VISIBLE);
            } else {
                messageBadge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onUnreadCountChanged(int newCount) {
        runOnUiThread(() -> updateNotificationsBadge(newCount));
    }



    @Override
    public void onUnreadMessageCountChanged(int newCount) {
        runOnUiThread(() -> updateMessageBadge(newCount));
    }



    private void loadMainFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commitAllowingStateLoss();
        Log.d(TAG, "loadMainFragment: Loaded " + fragment.getClass().getSimpleName());
    }

    private void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
        Log.d(TAG, "navigateToFragment: Navigated to " + fragment.getClass().getSimpleName());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void setupGuestUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.show(getSupportFragmentManager(), "loginFragment");
            } else if (id == R.id.nav_signup) {
                SignUpFragment signUpFragment = new SignUpFragment();
                signUpFragment.show(getSupportFragmentManager(), "signUpFragment");
            } else if (id == R.id.nav_home) {
                loadMainFragment(new HomepageFragment());
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupAuthenticatedUserUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.authenticated_user_menu);

        MenuItem notificationsItem = navigationView.getMenu().findItem(R.id.nav_notifications);
        if (notificationsItem != null) {
            View actionView = notificationsItem.getActionView();
            if (actionView != null) {
                notificationBadge = actionView.findViewById(R.id.notification_badge);
                if(notificationService != null){
                    updateNotificationsBadge(notificationService.getUnreadCount());
                }
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_view_profile) {
                navigateToFragment(new ProfileViewFragment());
            } else if (id == R.id.nav_log_out) {
                logOut();
            } else if (id == R.id.nav_invited_events) {
                navigateToFragment(new InvitedEventsListFragment());
            } else if (id == R.id.nav_calendar_od) {
                navigateToFragment(new CalendarFragment());
            } else if (id == R.id.nav_explore_events) {
                navigateToFragment(new ExplorePageFragment());
            } else if (id == R.id.nav_notifications) {
                updateNotificationsBadge(0);
                updateHamburgerIcon(false);
                navigateToFragment(new NotificationFragment());
            } else if (id == R.id.nav_home) {
                loadMainFragment(new HomepageFragment());
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupOrganizerUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.organizer_menu);

        MenuItem notificationsItem = navigationView.getMenu().findItem(R.id.nav_notifications);
        if (notificationsItem != null) {
            View actionView = notificationsItem.getActionView();
            if (actionView != null) {
                notificationBadge = actionView.findViewById(R.id.notification_badge);
                if(notificationService != null){
                    updateNotificationsBadge(notificationService.getUnreadCount());
                }
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadMainFragment(new HomepageFragment());
            } else if (id == R.id.nav_fav_events) {
                navigateToFragment(new FavoriteEventsFragment());
            } else if (id == R.id.nav_services) {
                navigateToFragment(new ServiceManagement());
            } else if (id == R.id.nav_view_profile) {
                navigateToFragment(new ProfileViewFragment());
            } else if (id == R.id.nav_calendar_od) {
                navigateToFragment(new CalendarFragment());
            } else if (id == R.id.nav_create_event) {
                navigateToFragment(new EventCreationFragment());
            } else if (id == R.id.nav_explore_events) {
                navigateToFragment(new ExplorePageFragment());
            } else if (id == R.id.nav_fav_services) {
                navigateToFragment(new FavoriteServicesFragment());
            } else if (id == R.id.nav_fav_products) {
                navigateToFragment(new FavoriteProductsFragment());
            } else if (id == R.id.nav_notifications) {
                updateNotificationsBadge(0);
                updateHamburgerIcon(false);
                navigateToFragment(new NotificationFragment());
            } else if (id == R.id.nav_service_reservations) {
                navigateToFragment(new ServiceReservationsManagementFragment());
            } else if (id == R.id.nav_log_out) {
                logOut();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupProviderUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.provider_menu);


        MenuItem notificationsItem = navigationView.getMenu().findItem(R.id.nav_notifications);
        if (notificationsItem != null) {
            View actionView = notificationsItem.getActionView();
            if (actionView != null) {
                notificationBadge = actionView.findViewById(R.id.notification_badge);
                if(notificationService != null){
                    updateNotificationsBadge(notificationService.getUnreadCount());
                }
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadMainFragment(new HomepageFragment());
            } else if (id == R.id.nav_create_business) {
                navigateToFragment(new BusinessRegistrationFragment());
            } else if (id == R.id.nav_business_info) {
                navigateToFragment(new BusinessInfoFragment());
            } else if (id == R.id.nav_products) {
                navigateToFragment(new ProvidedProductsFragment());
            } else if (id == R.id.nav_service) {
                navigateToFragment(new ServiceManagement());
            } else if (id == R.id.nav_calendar_od) {
                navigateToFragment(new CalendarFragment());
            } else if (id == R.id.nav_fav_events) {
                navigateToFragment(new FavoriteEventsFragment());
            } else if (id == R.id.nav_fav_services) {
                navigateToFragment(new FavoriteServicesFragment());
            } else if (id == R.id.nav_fav_products) {
                navigateToFragment(new FavoriteProductsFragment());
            } else if (id == R.id.nav_explore_events) {
                navigateToFragment(new ExplorePageFragment());
            } else if (id == R.id.nav_view_profile) {
                navigateToFragment(new ProfileViewFragment());
            } else if (id == R.id.nav_categories) {
                navigateToFragment(new CategoriesTableFragment());
            } else if (id == R.id.nav_event_types) {
                navigateToFragment(new EventTypeTableFragment());
            } else if (id == R.id.nav_notifications) {
                updateNotificationsBadge(0);
                updateHamburgerIcon(false);
                navigateToFragment(new NotificationFragment());
            } else if (id == R.id.nav_log_out) {
                logOut();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupAdminUI() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.admin_menu);

        MenuItem notificationsItem = navigationView.getMenu().findItem(R.id.nav_notifications);
        if (notificationsItem != null) {
            View actionView = notificationsItem.getActionView();
            if (actionView != null) {
                notificationBadge = actionView.findViewById(R.id.notification_badge);
                if(notificationService != null){
                    updateNotificationsBadge(notificationService.getUnreadCount());
                }
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadMainFragment(new HomepageFragment());
            }  else if (id == R.id.nav_event_types_overview) {
                navigateToFragment(new EventTypeTableFragment());
            } else if (id == R.id.nav_attendance_chart) {
                navigateToFragment(new AttendanceChartFragment());
            } else if (id == R.id.nav_ratings_chart) {
                navigateToFragment(new RatingsChartFragment());
            } else if (id == R.id.nav_notifications) {
                updateNotificationsBadge(0);
                updateHamburgerIcon(false);
                navigateToFragment(new NotificationFragment());
            } else if(id == R.id.nav_categories){
                navigateToFragment(new CategoriesContainerFragment());
            } else if (id == R.id.nav_manage_comments) {
                navigateToFragment(new CommentManagementFragment());
            } else if (id == R.id.nav_manage_reports) {
                navigateToFragment(new ReportManagementFragment());
            } else if (id == R.id.nav_log_out) {
                logOut();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void logOut() {
        new AlertDialog.Builder(this, R.style.RoundedAlertDialogTheme)
                .setTitle("Log out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("YES", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.remove("userRole");
                    editor.apply();

                    Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void loadSuspendedFragment() {
        SharedPreferences sp = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String days = sp.getString("suspensionDays", "0");
        String hours = sp.getString("suspensionHours", "0");
        String minutes = sp.getString("suspensionMinutes", "0");

        Bundle args = new Bundle();
        args.putString("days", days);
        args.putString("hours", hours);
        args.putString("minutes", minutes);

        SuspendedUserFragment suspendedFragment = new SuspendedUserFragment();
        suspendedFragment.setArguments(args);

        loadMainFragment(suspendedFragment);
    }

    private void updateHamburgerIcon(boolean showDot) {
        if (toolbar == null || toggle == null) return;

        if (showDot && !notificationsOpen) {
            DrawerArrowDrawable custom = new DrawerArrowDrawable(toolbar.getContext()) {
                @Override
                public void draw(Canvas canvas) {
                    super.draw(canvas);

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.RED);

                    float radius = 4 * getResources().getDisplayMetrics().density;

                    Rect bounds = getBounds();
                    float cx = bounds.right - radius - 2 * getResources().getDisplayMetrics().density;
                    float cy = bounds.top + radius + 2 * getResources().getDisplayMetrics().density;

                    canvas.drawCircle(cx, cy, radius, paint);
                }
            };

            toggle.setDrawerArrowDrawable(custom);
        } else {
            toggle.setDrawerArrowDrawable(originalDrawerIcon);

        }
    }


    public void onNotificationsOpened() {
        if (toggle != null) {
            notificationsOpen = true;
        }
    }

    public void onNotificationsClosed() {
        if (toggle != null) {
            notificationsOpen = false;
        }
    }
    public void onConversationOpened() {
        if (toggle != null) {
            conversationOpen = true;
        }
    }

    public void onConversationClosed() {
        if (toggle != null) {
            conversationOpen = false;
        }
    }

    private void enableChatDrawer() {
        View chatDrawer = findViewById(R.id.chat_drawer_root);
        FloatingActionButton chatButton = findViewById(R.id.open_chat_button);

        if (chatDrawer != null && chatButton != null) {
            chatDrawer.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);

            chatButton.setOnClickListener(v -> {
                loadChatFragment();
                conversationWebSocketService.markAllAsRead();
                drawerLayout.openDrawer(GravityCompat.END);
            });
        }
    }

    private void loadChatFragment() {
        Fragment chatFragment = new ConversationListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chat_fragment_container, chatFragment)
                .commitAllowingStateLoss();
    }

    public void openChatSidebar() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void openProfileAndCloseChat(String userEmail) {
        DrawerLayout drawerLayout = findViewById(R.id.navigationView);
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }

        ViewUserProfileFragment profileFragment = ViewUserProfileFragment.newInstance(userEmail);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, profileFragment)
                .addToBackStack(null)
                .commit();
    }

    public void closeChatSidebar() {
        DrawerLayout drawerLayout = findViewById(R.id.navigationView);
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    public NotificationWebSocketService getNotificationService() {
        return notificationService;
    }

    public ConversationWebSocketService getConversationService() {
        return conversationWebSocketService;
    }
}