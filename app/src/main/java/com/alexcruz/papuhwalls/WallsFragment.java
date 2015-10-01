package com.alexcruz.papuhwalls;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mrengineer13.snackbar.SnackBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class WallsFragment extends ActionBarActivity {

    private Toolbar toolbar;
    public String wall;
    private String saveWallLocation, picName, dialogContent;
    private View fabBg;
    private Context context;
    Preferences Preferences;

    private static final int ACTIVITY_SHARE = 13452;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.Preferences = new Preferences(getApplicationContext());
        setContentView(R.layout.wallpaper_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_ab_detailed_wallpaper);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        fabBg = findViewById(R.id.fabBg);
        fabBg.setVisibility(View.GONE);

        saveWallLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + context.getResources().getString(R.string.walls_save_location);
        picName = context.getResources().getString(R.string.walls_prefix_name);

        dialogContent = getResources().getString(R.string.download_done) + saveWallLocation;

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isfirstrun", true);

        if (isFirstRun) {

            File folder = new File(saveWallLocation);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isfirstrun", false).commit();

        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        scan(this, "external");

        fabBg = findViewById(R.id.fabBg);

        ImageView image = (ImageView) findViewById(R.id.bigwall);
        wall = getIntent().getStringExtra("wall");
        Picasso.with(this)
                .load(wall)
                .into(image, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                            }
                        }
                );

        final FloatingActionsMenu wallsFab = (FloatingActionsMenu) findViewById(R.id.wall_actions);
        wallsFab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                fabBg.setVisibility(fabBg.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                fabBg.setBackgroundColor(Preferences.FABbackground());
            }

            @Override
            public void onMenuCollapsed() {
                fabBg.setVisibility(fabBg.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        final FloatingActionButton setWall = (FloatingActionButton) findViewById(R.id.setwall);
        setWall.setColorNormal(Preferences.FABapply());
        setWall.setColorPressed(Preferences.FABpressed());
        setWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetWallDialog();
            }
        });

        final FloatingActionButton saveWall = (FloatingActionButton) findViewById(R.id.savewall);
        saveWall.setColorNormal(Preferences.FABsave());
        saveWall.setColorPressed(Preferences.FABpressed());
        saveWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Picasso.with(context)
                        .load(wall)
                        .into(target);

                showDownloadDialog(false);
            }
        });

        final FloatingActionButton cropWall = (FloatingActionButton) findViewById(R.id.cropwall);
        cropWall.setColorNormal(Preferences.FABcrop());
        cropWall.setColorPressed(Preferences.FABpressed());
        cropWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Picasso.with(context)
                        .load(wall)
                        .into(wallCropTarget);
                waitOnConnectedDialog();
            }
        });

        final FloatingActionButton editWall = (FloatingActionButton) findViewById(R.id.editwall);
        editWall.setColorNormal(Preferences.FABedit());
        editWall.setColorPressed(Preferences.FABpressed());
        editWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Picasso.with(context)
                        .load(wall)
                        .into(wallEditTarget);
                waitOnConnectedDialog();
            }
        });

        final FloatingActionButton shareWall = (FloatingActionButton) findViewById(R.id.sharewall);
        shareWall.setColorNormal(Preferences.FABshare());
        shareWall.setColorPressed(Preferences.FABpressed());
        shareWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Picasso.with(context)
                        .load(wall)
                        .into(wallShareTarget);
                waitOnConnectedDialog();
            }
        });
    }

    private void waitOnConnectedDialog() {
        new SnackBar.Builder(this)
                .withMessageId(R.string.wallpaper_downloading_wait)
                .withActionMessageId(R.string.ok)
                .withStyle(SnackBar.Style.ALERT)
                .withDuration(SnackBar.SHORT_SNACK)
                .show();
    }

    private com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    File file = new File(saveWallLocation, picName + convertWallName(wall) + ".png");
                    if (file.exists()) {
                        file.delete();
                    }

                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
                    .start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

            if (placeHolderDrawable != null) {

            }

        }
    };
    private com.squareup.picasso.Target wallTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WallpaperManager wm = WallpaperManager.getInstance(context);
                        wm.setBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
                    .start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

            if (placeHolderDrawable != null) {

            }

        }
    };

    private com.squareup.picasso.Target wallShareTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        ImageView wall = (ImageView) findViewById(R.id.bigwall);
                        Uri wallUri = getLocalBitmapUri(wall);
                        if (wallUri != null) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, wallUri);
                            WallsFragment.this.startActivityForResult(Intent.createChooser(shareIntent, "Share Via"), ACTIVITY_SHARE);
                        } else {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
                    .start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

            if (placeHolderDrawable != null) {

            }

        }
    };

    private com.squareup.picasso.Target wallCropTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        ImageView wall = (ImageView) findViewById(R.id.bigwall);
                        Uri wallUri = getLocalBitmapUri(wall);
                        if (wallUri != null) {
                            Intent setWall = new Intent(Intent.ACTION_ATTACH_DATA);
                            setWall.setDataAndType(wallUri, "image/*");
                            setWall.putExtra("png", "image/*");
                            startActivityForResult(Intent.createChooser(setWall, getString(R.string.set_as)), 1);
                        } else {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
                    .start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

            if (placeHolderDrawable != null) {

            }

        }
    };

    private com.squareup.picasso.Target wallEditTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        ImageView wall = (ImageView) findViewById(R.id.bigwall);
                        Uri wallUri = getLocalBitmapUri(wall);
                        if (wallUri != null) {
                            Intent editWall = new Intent(Intent.ACTION_EDIT);
                            editWall.setDataAndType(wallUri, "image/*");
                            editWall.putExtra("png", "image/*");
                            startActivityForResult(Intent.createChooser(editWall, getString(R.string.edit_wall)), 1);
                        } else {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
                    .start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

            if (placeHolderDrawable != null) {

            }

        }
    };

    private String convertWallName(String link) {
        return (link
                .replaceAll("png", "")
                .replaceAll("jpg", "")
                .replaceAll("jpeg", "")
                .replaceAll("bmp", "")
                .replaceAll("[^a-zA-Z0-9\\p{Z}]", "")
                .replaceFirst("^[0-9]+(?!$)", "")
                .replaceAll("\\p{Z}", "_"));
    }

    public void showDownloadDialog(boolean indeterminate) {

        if (indeterminate) {
            new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialog_title)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();;
            scan(this, "external");
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialog_title)
                    .content(R.string.please_wait)
                    .progress(false, 120)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (dialog.getCurrentProgress() != dialog.getMaxProgress()) {
                                        if (dialog.isCancelled())
                                            break;
                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            break;
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.incrementProgress(1);
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setTitle(getString(R.string.done));
                                            dialog.setContent(dialogContent);
                                            dialog.setActionButton(DialogAction.NEGATIVE, R.string.close);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }).show();
            scan(this, "external");
        }
    }

    public void showSetWallDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.set_wall_title)
                .content(R.string.set_wall_content)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        showSettingWallDialog(false);

                        Picasso.with(context)
                                .load(wall)
                                .into(wallTarget);

                    }
                })
                .show();
        scan(this, "external");
    }

    public void showSettingWallDialog(boolean indeterminate) {
        if (indeterminate) {
            new MaterialDialog.Builder(this)
                    .title(R.string.setting_wall_title)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
                     scan(this, "external");
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.setting_wall_title)
                    .content(R.string.please_wait)
                    .progress(false, 60)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (dialog.getCurrentProgress() != dialog.getMaxProgress()) {
                                        if (dialog.isCancelled())
                                            break;
                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            break;
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.incrementProgress(1);
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setTitle(getString(R.string.done));
                                            dialog.setContent(getString(R.string.set_as_wall_done));
                                            dialog.setActionButton(DialogAction.NEGATIVE, R.string.close);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }).show();
            scan(this, "external");
        }
    }

    private void showNoPicDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(R.string.wall_error)
                .positiveText(R.string.ok)
                .show();
    }

    private static void scan(Context context, String volume) {
        Bundle args = new Bundle();
        args.putString("volume", volume);
        context.startService(
                new Intent().
        setComponent(new ComponentName("com.android.providers.media", "com.android.providers.media.MediaScannerService")).
        putExtras(args));
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;

        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }

        Uri bmpUri = null;
        try {
            File file = new File(saveWallLocation, picName + convertWallName(wall) + ".png");
            file.getParentFile().mkdirs();
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            ;
        }
        return bmpUri;
    }

}
