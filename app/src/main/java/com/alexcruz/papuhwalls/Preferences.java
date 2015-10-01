package com.alexcruz.papuhwalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class Preferences {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String Theme = "Theme";
    public static final String NavBarTheme = "NavBarTheme";
    public static final String NavigationTint = "NavigationTint";
    public static final String Background = "Background";
    public static final String PrimaryText = "PrimaryText";
    public static final String SecondaryText = "SecondaryText";
    public static final String Accent = "Accent";
    public static final String Drawer = "Drawer";
    public static final String DrawerText = "DrawerText";
    public static final String SelectedDrawerText = "SelectedDrawerText";
    public static final String DrawerSelector = "DrawerSelector";
    public static final String BadgeBackground = "BadgeBackground";
    public static final String BadgeText = "BadgeText";
    public static final String FABapply = "FABapply";
    public static final String FABsave = "FABsave";
    public static final String FABcrop = "FABcrop";
    public static final String FABedit = "FABedit";
    public static final String FABshare = "FABshare";
    public static final String FABpressed = "FABpressed";
    public static final String FABbackground = "FABbackground";
    public static final String NormalIcon = "NormalIcon";
    public static final String SelectedIcon = "SelectedIcon";

    public int Theme() {
        return sharedPreferences.getInt(Theme, context.getResources().getColor(R.color.primary));
    }

    public int NavBarTheme() {
        return sharedPreferences.getInt(NavBarTheme, context.getResources().getColor(R.color.primary));
    }

    public Boolean getNavigationTint() {
        return sharedPreferences.getBoolean(NavigationTint, true);
    }

    public void setNavigationInt(Boolean res) {
        editor.putBoolean(NavigationTint, res);
        editor.commit();
    }

    public int Background() {
        return sharedPreferences.getInt(Background, context.getResources().getColor(R.color.navigation_drawer_color));
    }

    public int PrimaryText() {
        return sharedPreferences.getInt(PrimaryText, context.getResources().getColor(R.color.primary));
    }

    public int SecondaryText() {
        return sharedPreferences.getInt(SecondaryText, context.getResources().getColor(R.color.semitransparent_white));
    }

    public int Accent() {
        return sharedPreferences.getInt(Accent, context.getResources().getColor(R.color.accent));
    }

    public int Drawer() {
        return sharedPreferences.getInt(Drawer, context.getResources().getColor(R.color.navigation_drawer_color));
    }

    public int NormalIcon() {
        return sharedPreferences.getInt(NormalIcon, context.getResources().getColor(R.color.white));
    }

    public int SelectedIcon() {
        return sharedPreferences.getInt(SelectedIcon, context.getResources().getColor(R.color.primary));
    }

    public int DrawerText() {
        return sharedPreferences.getInt(DrawerText, context.getResources().getColor(R.color.white));
    }

    public int SelectedDrawerText() {
        return sharedPreferences.getInt(SelectedDrawerText, context.getResources().getColor(R.color.primary));
    }

    public int DrawerSelector() {
        return sharedPreferences.getInt(DrawerSelector, context.getResources().getColor(R.color.selector_drawer_color));
    }

    public int BadgeBackground() {
        return sharedPreferences.getInt(BadgeBackground, context.getResources().getColor(R.color.primary));
    }

    public int BadgeText() {
        return sharedPreferences.getInt(BadgeText, context.getResources().getColor(R.color.white));
    }

    public int FABapply() {
        return sharedPreferences.getInt(FABapply, context.getResources().getColor(R.color.apply_color));
    }

    public int FABsave() {
        return sharedPreferences.getInt(FABsave, context.getResources().getColor(R.color.save_color));
    }

    public int FABcrop() {
        return sharedPreferences.getInt(FABcrop, context.getResources().getColor(R.color.crop_color));
    }

    public int FABedit() {
        return sharedPreferences.getInt(FABedit, context.getResources().getColor(R.color.edit_color));
    }

    public int FABshare() {
        return sharedPreferences.getInt(FABshare, context.getResources().getColor(R.color.share_color));
    }

    public int FABpressed() {
        return sharedPreferences.getInt(FABpressed, context.getResources().getColor(R.color.primary));
    }

    public int FABbackground() {
        return sharedPreferences.getInt(FABbackground, context.getResources().getColor(R.color.black_semi_transparent));
    }

    String wall_name, wall_author, wall_url;

    public Preferences (String wall_name, String wall_author, String wall_url) {
        this.wall_name = wall_name;
        this.wall_author = wall_author;
        this.wall_url = wall_url;
    }

    public String getWallName() {
        return wall_name;
    }

    public String getWallAuthor() {
        return wall_author;
    }

    public String getWallURL() {
        return wall_url;
    }

    private static final String
            PREFERENCES_NAME = "PAPUH_PREFERENCES";

    private static final String
            ROTATE_MINUTE = "rotate_time_minute",
            ROTATE_TIME = "muzei_rotate_time";

    private Context context;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isRotateMinute() {
        return getSharedPreferences().getBoolean(ROTATE_MINUTE, false);
    }

    public static final String IS_FIRST__RUN="first_run";

    public int getRotateTime() {
        return getSharedPreferences().getInt(ROTATE_TIME, 900000);
    }

    public void setRotateTime (int time) {
        getSharedPreferences().edit().putInt(ROTATE_TIME, time).apply();
    }

    public void setRotateMinute (boolean bool) {
        getSharedPreferences().edit().putBoolean(ROTATE_MINUTE, bool).apply();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
