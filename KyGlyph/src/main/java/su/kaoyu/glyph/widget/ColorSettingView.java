package su.kaoyu.glyph.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import su.kaoyu.glyph.R;

public class ColorSettingView extends LinearLayout implements View.OnClickListener {
    private View background;
    private TextView tvTitle;
    private TextView tvDescription;
    private SquareColorView mColorView;
    private ColorPicker.OnColorChangedListener mColorChangedListener = new ColorPicker.OnColorChangedListener() {
        @Override
        public void onColorChanged(int color) {
            setColor(color);
        }
    };
    private int mColor = Color.TRANSPARENT;

    public ColorSettingView(Context context) {
        super(context);
        initView(context);
    }

    public ColorSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ColorSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorSettingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.color_setting, this);
        background = findViewById(R.id.background);
        tvTitle = ((TextView) findViewById(R.id.title));
        tvDescription = ((TextView) findViewById(R.id.description));
        mColorView = (SquareColorView) findViewById(R.id.color);
        setOnClickListener(this);

        setBackgroundColor(Color.BLACK);
        setTitleColor(Color.WHITE);
        setDescriptionColor(Color.GRAY);
    }

    public void setBackgroundColor(int color) {
        background.setBackgroundColor(color);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setTitleSize(float size) {
        tvTitle.setTextSize(size);
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setDescription(CharSequence description) {
        tvDescription.setText(description);
    }

    public void setDescriptionSize(float size) {
        tvDescription.setTextSize(size);
    }

    public void setDescriptionColor(int color) {
        tvDescription.setTextColor(color);
    }

    public void setColor(int color) {
        this.mColor = color;
        mColorView.setColor(color);
    }

    public void setOnColorChangedListener(ColorPicker.OnColorChangedListener listener) {
        mColorChangedListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mColorChangedListener != null) {
            showColorPickerDialog(getContext(), mColor, mColorChangedListener);
        }
    }

    private void showColorPickerDialog(Context context, int preColor, final ColorPicker.OnColorChangedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        final ColorPicker picker = new ColorPicker(context);
        picker.setLayoutParams(params);
        OpacityBar opacityBar = new OpacityBar(context);
        opacityBar.setLayoutParams(params);
        SaturationBar saturationBar = new SaturationBar(context);
        saturationBar.setLayoutParams(params);
        ValueBar valueBar = new ValueBar(context);
        valueBar.setLayoutParams(params);
        linearLayout.addView(picker);
        linearLayout.addView(opacityBar);
        linearLayout.addView(saturationBar);
        linearLayout.addView(valueBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);
        picker.setOldCenterColor(preColor);
        picker.setColor(preColor);
        builder.setView(linearLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setColor(picker.getColor());
                listener.onColorChanged(picker.getColor());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
