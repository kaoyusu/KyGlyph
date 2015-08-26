package su.kaoyu.glyph;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;

import su.kaoyu.glyph.dao.DBManager;
import su.kaoyu.glyph.widget.ColorSettingView;

public class MainActivity extends Activity {
    private DBManager dbManager;
    private View enableAutoHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = DBManager.getInstanceAndInit(this);

        enableAutoHide = findViewById(R.id.enableAutoHide);
        enableAutoHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        ColorSettingView menuColor = (ColorSettingView) findViewById(R.id.MenuColor);
        menuColor.setTitle("MenuColor");
        menuColor.setDescription("Set the float icon's background color");
        menuColor.setColor(dbManager.getMenuColor());
        menuColor.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                dbManager.setMenuColor(i);
            }
        });

        ColorSettingView inputBgColor = (ColorSettingView) findViewById(R.id.InputBgColor);
        inputBgColor.setTitle("InputBgColor");
        inputBgColor.setDescription("Set the color of input background");
        inputBgColor.setColor(dbManager.getInputBgColor());
        inputBgColor.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                dbManager.setInputBgColor(i);
            }
        });

        ColorSettingView inputColor = (ColorSettingView) findViewById(R.id.InputColor);
        inputColor.setTitle("InputColor");
        inputColor.setDescription("Set the color of input dots and lines");
        inputColor.setColor(dbManager.getInputColor());
        inputColor.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                dbManager.setInputColor(i);
            }
        });

        ColorSettingView glyphBgColor = (ColorSettingView) findViewById(R.id.GlyphBgColor);
        glyphBgColor.setTitle("GlyphBgColor");
        glyphBgColor.setDescription("Set the color of glyph's background");
        glyphBgColor.setColor(dbManager.getGlyphBgColor());
        glyphBgColor.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                dbManager.setGlyphBgColor(i);
            }
        });

        ColorSettingView glyphLineColor = (ColorSettingView) findViewById(R.id.GlyphLineColor);
        glyphLineColor.setTitle("GlyphLineColor");
        glyphLineColor.setDescription("Set the color of glyph's line");
        glyphLineColor.setColor(dbManager.getGlyphLineColor());
        glyphLineColor.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                dbManager.setGlyphLineColor(i);
            }
        });

        ColorSettingView glyphDotColor = (ColorSettingView) findViewById(R.id.GlyphDotColor);
        glyphDotColor.setTitle("GlyphDotColor");
        glyphDotColor.setDescription("Set the color of glyph's dot");
        glyphDotColor.setColor(dbManager.getGlyphDotColor());
        glyphDotColor.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                dbManager.setGlyphDotColor(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (dbManager.isAccessibilityEnabled()) {
            enableAutoHide.setVisibility(View.GONE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbManager.updateDatabase(new DBManager.OnDatabaseUpdatedListener() {
                    ProgressDialog progressDialog;

                    @Override
                    public void onDatabaseUpdating(final String name) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null) {
                                    progressDialog.setMessage("updating database by using " + name);
                                } else {
                                    progressDialog = ProgressDialog.show(MainActivity.this,
                                            "Updating Database", "updating database by using " + name);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDatabaseUpdated() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                startService(new Intent(MainActivity.this, GlyphService.class));
            }
        }).start();
    }
}
