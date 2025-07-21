//Please don't replace listeners with lambda!

package com.android.support;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;

import org.xml.sax.ErrorHandler;

public class Menu {
    //********** Premium Galaxy Dark Theme **********//

    //region Variable
    public static final String TAG = "Mod_Menu"; //Tag for logcat

    // Galaxy Dark Theme Colors
    int TEXT_COLOR = Color.parseColor("#E3F2FD"); // Light blue text
    int TEXT_COLOR_2 = Color.parseColor("#FFFFFF"); // Pure white
    int TEXT_COLOR_ACCENT = Color.parseColor("#BB86FC"); // Purple accent
    int BTN_COLOR = Color.parseColor("#1E1E2E"); // Dark button background
    int BTN_HOVER_COLOR = Color.parseColor("#2D2D42"); // Button hover state
    int MENU_BG_COLOR = Color.parseColor("#EE0A0A14"); // Deep space dark
    int MENU_FEATURE_BG_COLOR = Color.parseColor("#DD161622"); // Feature background
    int CARD_BG_COLOR = Color.parseColor("#1A1A2E"); // Card background
    int MENU_WIDTH = 320;
    int MENU_HEIGHT = 240;
    int POS_X = 0;
    int POS_Y = 100;

    float MENU_CORNER = 12f; // Rounded corners
    int ICON_SIZE = 50; // Larger icon
    float ICON_ALPHA = 0.9f; // Less transparent
    int ToggleON = Color.parseColor("#00E676"); // Bright green
    int ToggleOFF = Color.parseColor("#F44336"); // Material red
    int BtnON = Color.parseColor("#00C853"); // Success green
    int BtnOFF = Color.parseColor("#D32F2F"); // Error red
    int CategoryBG = Color.parseColor("#232338"); // Category background
    int SeekBarColor = Color.parseColor("#BB86FC"); // Purple seekbar
    int SeekBarProgressColor = Color.parseColor("#7C4DFF"); // Deep purple progress
    int CheckBoxColor = Color.parseColor("#BB86FC"); // Purple checkbox
    int RadioColor = Color.parseColor("#E1BEE7"); // Light purple radio
    String NumberTxtColor = "#00E676"; // Bright green numbers

    // Premium UI Enhancement Colors
    int BORDER_COLOR = Color.parseColor("#3700B3"); // Purple border
    int SHADOW_COLOR = Color.parseColor("#33000000"); // Soft shadow
    int GRADIENT_START = Color.parseColor("#1A1A2E");
    int GRADIENT_END = Color.parseColor("#16213E");
    //********************************************************************//

    RelativeLayout mCollapsed, mRootContainer;
    LinearLayout mExpanded, mods, mSettings, mCollapse;
    LinearLayout.LayoutParams scrlLLExpanded, scrlLL;
    WindowManager mWindowManager;
    WindowManager.LayoutParams vmParams;
    ImageView startimage;
    FrameLayout rootFrame;
    ScrollView scrollView;
    boolean stopChecking, overlayRequired;
    Context getContext;
    
    // Performance optimization: Use shared thread pool and handler
    private static ExecutorService executorService;
    private Handler mainHandler;

    //initialize methods from the native library
    native void Init(Context context, TextView title, TextView subTitle);

    native String Icon();

    native String IconWebViewData();

    native String[] GetFeatureList();

    native String[] SettingsList();

    native boolean IsGameLibLoaded();

    native void onMenuSelected(int menuIndex);

    native String getTextBoxContent(int featNum);

    native int getCurrentMenuIndex();

    //Here we write the code for our Menu
    // Reference: https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
    public Menu(Context context) {

        getContext = context;
        Preferences.context = context;
        
        // Initialize performance optimizations
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newCachedThreadPool();
        }
        mainHandler = new Handler(Looper.getMainLooper());
        rootFrame = new FrameLayout(context); // Global markup
        rootFrame.setOnTouchListener(onTouchListener());
        mRootContainer = new RelativeLayout(context); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(context); // Markup of the icon (when the menu is minimized)
        mCollapsed.setVisibility(View.VISIBLE);
        mCollapsed.setAlpha(ICON_ALPHA);

        //********** Enhanced Premium Galaxy Dark Menu Container **********
        mExpanded = new LinearLayout(context); // Menu markup (when the menu is expanded)
        mExpanded.setVisibility(View.GONE);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        mExpanded.setPadding(6, 6, 6, 6); // Enhanced padding for modern look
        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(MENU_WIDTH), WRAP_CONTENT));

        // Enhanced gradient background with modern styling
        GradientDrawable gdMenuBody = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, // Diagonal gradient for depth
            new int[]{GRADIENT_START, MENU_BG_COLOR, GRADIENT_END}
        );
        gdMenuBody.setCornerRadius(MENU_CORNER + 4); // Slightly more rounded
        gdMenuBody.setStroke(3, BORDER_COLOR); // Thicker border for premium look
        
        // Enhanced shadow and elevation for modern depth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mExpanded.setElevation(12f); // Increased elevation
            mExpanded.setTranslationZ(4f); // Additional depth
        }
        mExpanded.setBackground(gdMenuBody);
        
        // Add subtle inner glow effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mExpanded.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            mExpanded.setClipToOutline(true);
        }

        //********** Enhanced Premium Icon to open mod menu **********
        startimage = new ImageView(context);
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension = (int) TypedValue.applyDimension(1, ICON_SIZE + 8, context.getResources().getDisplayMetrics()); // Larger icon
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        
        // Enhanced icon styling with modern effects
        byte[] decode = Base64.decode(Icon(), 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        
        // Add modern circular background
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.parseColor("#BB86FC")); // Purple glow background
        iconBg.setStroke(3, Color.parseColor("#FFFFFF")); // White border
        startimage.setBackground(iconBg);
        
        // Add modern elevation and shadow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startimage.setElevation(8f);
            startimage.setTranslationZ(4f);
        }
        
        // Better positioning
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(12);
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).setMargins(
            convertDipToPixels(4), convertDipToPixels(12), convertDipToPixels(4), convertDipToPixels(4)
        );
        
        // Enhanced interaction
        startimage.setOnTouchListener(onTouchListener());
        startimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Add smooth transition animation
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mCollapsed.animate().alpha(0f).setDuration(200).start();
                    mExpanded.setAlpha(0f);
                    mExpanded.setVisibility(View.VISIBLE);
                    mExpanded.animate().alpha(1f).setDuration(300).start();
                } else {
                    mCollapsed.setVisibility(View.GONE);
                    mExpanded.setVisibility(View.VISIBLE);
                }
            }
        });

        //********** Enhanced Premium WebView Icon to open mod menu **********
        WebView wView = new WebView(context);
        wView.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension2 = (int) TypedValue.applyDimension(1, ICON_SIZE + 8, context.getResources().getDisplayMetrics()); // Larger icon
        wView.getLayoutParams().height = applyDimension2;
        wView.getLayoutParams().width = applyDimension2;
        
        // Enhanced WebView HTML with modern styling
        String iconHtml = "<html>" +
                "<head>" +
                "<style>" +
                "body { margin: 0; padding: 8px; background: linear-gradient(45deg, #BB86FC, #9C5AFF); border-radius: 50%; border: 3px solid #FFFFFF; box-shadow: 0 4px 12px rgba(0,0,0,0.3); }" +
                "img { width: " + (ICON_SIZE - 4) + "px; height: " + (ICON_SIZE - 4) + "px; border-radius: 50%; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<img src=\"" + IconWebViewData() + "\" />" +
                "</body>" +
                "</html>";
        wView.loadData(iconHtml, "text/html", "utf-8");
        
        wView.setBackgroundColor(0x00000000); // Transparent
        wView.setAlpha(ICON_ALPHA + 0.1f); // Slightly more visible
        
        // Add modern elevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wView.setElevation(8f);
            wView.setTranslationZ(4f);
        }
        
        // Better positioning
        ((ViewGroup.MarginLayoutParams) wView.getLayoutParams()).setMargins(
            convertDipToPixels(4), convertDipToPixels(12), convertDipToPixels(4), convertDipToPixels(4)
        );
        
        wView.setOnTouchListener(onTouchListener());

        //********** Enhanced Menu Navigation Buttons **********
        final LinearLayout menuNavigation = new LinearLayout(context);
        menuNavigation.setOrientation(LinearLayout.HORIZONTAL);
        menuNavigation.setPadding(16, 12, 16, 12); // More spacious padding
        menuNavigation.setGravity(Gravity.CENTER);

        // Enhanced navigation background with modern styling
        GradientDrawable navBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{CARD_BG_COLOR, Color.parseColor("#1F1F33"), CARD_BG_COLOR}
        );
        navBg.setCornerRadius(12f); // More rounded for modern look
        navBg.setStroke(2, BORDER_COLOR); // Thicker border
        
        // Add subtle inner shadow effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            menuNavigation.setElevation(4f);
        }
        menuNavigation.setBackground(navBg);
        
        // Add margin for floating effect
        LinearLayout.LayoutParams navParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        navParams.setMargins(8, 4, 8, 8);
        menuNavigation.setLayoutParams(navParams);

        // Only 3 menus with custom names
        String[] menuTitles = {"AIMBOT", "WALLHACK", "EXTRAS"};
        final Button[] menuButtons = new Button[3];

        // Track currently active menu button index (default to 0)
        final int[] currentActiveMenuIndex = {0};

        for (int i = 0; i < 3; i++) {
            final int menuIndex = i + 1;
            final int buttonIndex = i;
            Button menuBtn = new Button(context);
            menuBtn.setText(menuTitles[i]);
            menuBtn.setTextColor(TEXT_COLOR_2);
            menuBtn.setTextSize(11.5f); // Slightly larger text
            menuBtn.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
            
            // Enhanced menu button styling with modern design
            GradientDrawable menuBtnBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{BTN_COLOR, BTN_HOVER_COLOR}
            );
            menuBtnBg.setCornerRadius(18f); // More rounded
            menuBtnBg.setStroke(2, BORDER_COLOR); // Thicker border
            
            // Add modern shadow effect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menuBtn.setElevation(3f);
                menuBtn.setStateListAnimator(null); // Remove default animation
            }
            menuBtn.setBackground(menuBtnBg);

            LinearLayout.LayoutParams menuBtnParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
            menuBtnParams.weight = 1.0f;
            menuBtnParams.setMargins(4, 2, 4, 2); // Better spacing
            menuBtn.setLayoutParams(menuBtnParams);
            menuBtn.setPadding(12, 12, 12, 12); // More padding for better touch area
            
            // Add subtle text shadow for depth
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menuBtn.setLetterSpacing(0.05f);
            }

            menuButtons[i] = menuBtn;

            menuBtn.setOnClickListener(new View.OnClickListener() {
                private boolean isProcessing = false;
                private long lastClickTime = 0;

                @Override
                public void onClick(View v) {
                    // Debouncing - prevent multiple rapid clicks
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 300 || isProcessing) {
                        return;
                    }
                    lastClickTime = currentTime;
                    isProcessing = true;

                    // Disable all menu buttons temporarily
                    for (Button btn : menuButtons) {
                        btn.setEnabled(false);
                        btn.setAlpha(0.7f);
                    }

                    // Update currently active menu index
                    currentActiveMenuIndex[0] = buttonIndex;

                    // Update visual state immediately for better responsiveness
                    for (int j = 0; j < menuButtons.length; j++) {
                        if (j == buttonIndex) {
                            // Enhanced active button styling with glow effect
                            GradientDrawable activeBg = new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[]{TEXT_COLOR_ACCENT, Color.parseColor("#9C5AFF"), TEXT_COLOR_ACCENT}
                            );
                            activeBg.setCornerRadius(18f);
                            activeBg.setStroke(3, Color.parseColor("#FFFFFF"));
                            menuButtons[j].setBackground(activeBg);
                            menuButtons[j].setTextColor(Color.parseColor("#FFFFFF"));
                            
                            // Add glow effect for active button
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                menuButtons[j].setElevation(6f);
                                menuButtons[j].setTranslationZ(2f);
                            }
                        } else {
                            // Enhanced inactive button styling
                            GradientDrawable inactiveBg = new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[]{BTN_COLOR, BTN_HOVER_COLOR}
                            );
                            inactiveBg.setCornerRadius(18f);
                            inactiveBg.setStroke(2, BORDER_COLOR);
                            menuButtons[j].setBackground(inactiveBg);
                            menuButtons[j].setTextColor(TEXT_COLOR_2);
                            
                            // Reset elevation for inactive buttons
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                menuButtons[j].setElevation(3f);
                                menuButtons[j].setTranslationZ(0f);
                            }
                        }
                    }

                    // Use cached executor for better performance
                    executorService.execute(() -> {
                        try {
                            // Call native method to handle menu selection
                            onMenuSelected(menuIndex);

                            // Update UI on main thread with optimized operations
                            mainHandler.post(() -> {
                                try {
                                    // Clear and refresh menu content efficiently
                                    if (mods.getChildCount() > 0) {
                                        mods.removeAllViews();
                                    }
                                    
                                    // Get features and populate
                                    String[] features = GetFeatureList();
                                    if (features != null && features.length > 0) {
                                        featureList(features, mods);
                                    }
                                    
                                    // Smooth scroll to top
                                    scrollView.smoothScrollTo(0, 0);
                                    
                                } catch (Exception e) {
                                    Log.e(TAG, "Error refreshing menu content", e);
                                } finally {
                                    // Re-enable buttons efficiently
                                    for (Button btn : menuButtons) {
                                        btn.setEnabled(true);
                                        btn.setAlpha(1.0f);
                                    }
                                    isProcessing = false;
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "Error in menu selection", e);
                            // Re-enable buttons on error
                            mainHandler.post(() -> {
                                for (Button btn : menuButtons) {
                                    btn.setEnabled(true);
                                    btn.setAlpha(1.0f);
                                }
                                isProcessing = false;
                            });
                        }
                    });
                }
            });

            menuNavigation.addView(menuBtn);
        }

        // Set enhanced initial active state for first menu
        if (menuButtons.length > 0) {
            // Enhanced active button styling with glow effect
            GradientDrawable activeBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{TEXT_COLOR_ACCENT, Color.parseColor("#9C5AFF"), TEXT_COLOR_ACCENT}
            );
            activeBg.setCornerRadius(18f);
            activeBg.setStroke(3, Color.parseColor("#FFFFFF"));
            menuButtons[0].setBackground(activeBg);
            menuButtons[0].setTextColor(Color.parseColor("#FFFFFF"));
            
            // Add initial glow effect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menuButtons[0].setElevation(6f);
                menuButtons[0].setTranslationZ(2f);
            }
        }

        //********** Settings **********
        mSettings = new LinearLayout(context);
        mSettings.setOrientation(LinearLayout.VERTICAL);
        featureList(SettingsList(), mSettings);

        //********** Enhanced Premium Galaxy Title with Animated Elements **********
        RelativeLayout titleText = new RelativeLayout(getContext);
        titleText.setPadding(20, 16, 20, 16); // More spacious padding
        titleText.setGravity(Gravity.CENTER);

        // Enhanced title background with modern glass effect
        GradientDrawable titleBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.parseColor("#1E1E2E"), CARD_BG_COLOR, Color.parseColor("#1E1E2E")}
        );
        titleBg.setCornerRadius(14f); // More rounded for modern look
        titleBg.setStroke(2, BORDER_COLOR); // Thicker border
        
        // Add elevation and glow effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleText.setElevation(6f);
            titleText.setTranslationZ(2f);
        }
        titleText.setBackground(titleBg);
        
        // Add margin for floating effect
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        titleParams.setMargins(8, 4, 8, 4);
        titleText.setLayoutParams(titleParams);

        // Enhanced Left Decorative Element with modern design
        TextView leftCircle = new TextView(context);
        leftCircle.setText("◆"); // Diamond shape for modern look
        leftCircle.setTextColor(TEXT_COLOR_ACCENT);
        leftCircle.setTextSize(18.0f); // Slightly larger
        leftCircle.setGravity(Gravity.CENTER);
        leftCircle.setTypeface(Typeface.DEFAULT_BOLD);
        
        // Add subtle glow effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            leftCircle.setElevation(2f);
        }
        
        RelativeLayout.LayoutParams leftCircleParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        leftCircleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftCircleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftCircleParams.setMargins(12, 0, 0, 0); // More spacing
        leftCircle.setLayoutParams(leftCircleParams);

        // Enhanced Title with modern typography
        TextView title = new TextView(context);
        title.setTextColor(TEXT_COLOR_ACCENT);
        title.setTextSize(22.0f); // Larger for better visibility
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        
        // Enhanced typography settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            title.setLetterSpacing(0.15f); // Increased spacing for premium look
            title.setElevation(2f); // Subtle elevation
        }
        
        // Add subtle text shadow for depth
        title.setShadowLayer(4f, 0f, 2f, Color.parseColor("#33000000"));
        
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rl.addRule(RelativeLayout.CENTER_VERTICAL);
        title.setLayoutParams(rl);

        // Enhanced Right Decorative Element
        TextView rightCircle = new TextView(context);
        rightCircle.setText("◆"); // Diamond shape for modern look
        rightCircle.setTextColor(TEXT_COLOR_ACCENT);
        rightCircle.setTextSize(18.0f); // Slightly larger
        rightCircle.setGravity(Gravity.CENTER);
        rightCircle.setTypeface(Typeface.DEFAULT_BOLD);
        
        // Add subtle glow effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rightCircle.setElevation(2f);
        }
        
        RelativeLayout.LayoutParams rightCircleParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rightCircleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightCircleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rightCircleParams.setMargins(0, 0, 12, 0); // More spacing
        rightCircle.setLayoutParams(rightCircleParams);

        // Add smooth pulsing animation for circles
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator leftPulse = ObjectAnimator.ofFloat(leftCircle, "alpha", 0.3f, 1.0f);
            leftPulse.setDuration(1500);
            leftPulse.setRepeatCount(ObjectAnimator.INFINITE);
            leftPulse.setRepeatMode(ObjectAnimator.REVERSE);
            leftPulse.start();

            ObjectAnimator rightPulse = ObjectAnimator.ofFloat(rightCircle, "alpha", 0.3f, 1.0f);
            rightPulse.setDuration(1500);
            rightPulse.setRepeatCount(ObjectAnimator.INFINITE);
            rightPulse.setRepeatMode(ObjectAnimator.REVERSE);
            rightPulse.setStartDelay(750); // Offset animation
            rightPulse.start();
        }

        //********** Enhanced Premium Subtitle **********
        TextView subTitle = new TextView(context);
        subTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        subTitle.setMarqueeRepeatLimit(-1);
        subTitle.setSingleLine(true);
        subTitle.setSelected(true);
        subTitle.setTextColor(TEXT_COLOR);
        subTitle.setTextSize(13.0f); // Slightly larger for better readability
        subTitle.setGravity(Gravity.CENTER);
        subTitle.setPadding(20, 12, 20, 16); // More generous padding
        subTitle.setTypeface(Typeface.create("sans-serif-light", Typeface.ITALIC)); // Italic for elegance
        
        // Enhanced typography and effects
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            subTitle.setLetterSpacing(0.08f); // More spacing
            subTitle.setElevation(1f); // Subtle elevation
        }
        
        // Add subtle text shadow
        subTitle.setShadowLayer(2f, 0f, 1f, Color.parseColor("#22000000"));
        
        // Create subtle background for subtitle
        GradientDrawable subTitleBg = new GradientDrawable();
        subTitleBg.setCornerRadius(8f);
        subTitleBg.setColor(Color.parseColor("#15FFFFFF")); // Subtle white overlay
        subTitle.setBackground(subTitleBg);
        
        // Add margin for spacing
        LinearLayout.LayoutParams subTitleParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        subTitleParams.setMargins(12, 4, 12, 8);
        subTitle.setLayoutParams(subTitleParams);

        //********** Enhanced Premium Feature List Container **********
        scrollView = new ScrollView(context);
        scrlLL = new LinearLayout.LayoutParams(MATCH_PARENT, dp(MENU_HEIGHT));
        scrlLLExpanded = new LinearLayout.LayoutParams(mExpanded.getLayoutParams());
        scrlLLExpanded.weight = 1.0f;
        scrollView.setLayoutParams(Preferences.isExpanded ? scrlLLExpanded : scrlLL);
        scrollView.setPadding(12, 12, 12, 12); // More padding for better content spacing

        // Enhanced scroll view background with modern glass effect
        GradientDrawable scrollBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{MENU_FEATURE_BG_COLOR, Color.parseColor("#1A1628"), MENU_FEATURE_BG_COLOR}
        );
        scrollBg.setCornerRadius(12f); // More rounded corners
        scrollBg.setStroke(2, Color.parseColor("#3A3A5E")); // Better border color
        
        // Add modern elevation and shadow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scrollView.setElevation(2f);
            scrollView.setNestedScrollingEnabled(true); // Better scroll performance
        }
        scrollView.setBackground(scrollBg);
        
        // Enhanced scroll performance
        scrollView.setVerticalScrollBarEnabled(false); // Hide scrollbar for cleaner look
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER); // Remove overscroll glow
        scrollView.setScrollbarFadingEnabled(true);
        
        // Add margin for floating effect
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(MATCH_PARENT, 
            Preferences.isExpanded ? 0 : dp(MENU_HEIGHT));
        if (Preferences.isExpanded) {
            scrollParams.weight = 1.0f;
        }
        scrollParams.setMargins(8, 4, 8, 8);
        scrollView.setLayoutParams(scrollParams);

        // Enhanced main content container
        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);
        mods.setPadding(12, 12, 12, 12); // More padding for better spacing
        
        // Add subtle background for content separation
        GradientDrawable modsBg = new GradientDrawable();
        modsBg.setCornerRadius(8f);
        modsBg.setColor(Color.parseColor("#08FFFFFF")); // Very subtle white overlay
        mods.setBackground(modsBg);

        //********** Enhanced Bottom Button Layout **********
        LinearLayout bottomButtonLayout = new LinearLayout(context);
        bottomButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomButtonLayout.setPadding(12, 10, 12, 10); // More padding for better touch targets
        bottomButtonLayout.setGravity(Gravity.CENTER);

        // Enhanced bottom layout background with modern design
        GradientDrawable bottomBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.parseColor("#1A1A2E"), CARD_BG_COLOR, Color.parseColor("#1A1A2E")}
        );
        bottomBg.setCornerRadius(12f); // More rounded for modern look
        bottomBg.setStroke(2, BORDER_COLOR); // Thicker border
        
        // Add elevation for floating effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottomButtonLayout.setElevation(4f);
        }
        bottomButtonLayout.setBackground(bottomBg);
        
        // Add margin for floating effect
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        bottomParams.setMargins(8, 8, 8, 4);
        bottomButtonLayout.setLayoutParams(bottomParams);

        //**********  Enhanced Premium Hide/Kill Button **********
        Button hideBtn = new Button(context);
        hideBtn.setText("HIDE");
        hideBtn.setTextColor(TEXT_COLOR_2);
        hideBtn.setTextSize(10.5f); // Slightly larger text
        hideBtn.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

        // Enhanced button styling with modern gradient
        GradientDrawable hideBtnBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{BTN_COLOR, Color.parseColor("#1A1A2E")}
        );
        hideBtnBg.setCornerRadius(18f); // More rounded
        hideBtnBg.setStroke(2, Color.parseColor("#F44336")); // Thicker border
        
        // Add modern elevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hideBtn.setElevation(3f);
            hideBtn.setStateListAnimator(null);
        }
        hideBtn.setBackground(hideBtnBg);
        hideBtn.setPadding(16, 10, 16, 10); // Better padding

        LinearLayout.LayoutParams hideBtnParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        hideBtnParams.weight = 1.0f;
        hideBtnParams.setMargins(6, 2, 3, 2); // Better spacing
        hideBtn.setLayoutParams(hideBtnParams);

        hideBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.VISIBLE);
                mCollapsed.setAlpha(0);
                mExpanded.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Icon hidden. Remember the hidden icon position", Toast.LENGTH_LONG).show();
            }
        });
        hideBtn.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "Menu killed", Toast.LENGTH_LONG).show();
                rootFrame.removeView(mRootContainer);
                mWindowManager.removeView(rootFrame);
                return false;
            }
        });

        //********** Enhanced Premium Settings Button **********
        Button settingsBtn = new Button(context);
        settingsBtn.setText("SETTINGS");
        settingsBtn.setTextColor(TEXT_COLOR_ACCENT);
        settingsBtn.setTextSize(10.5f); // Slightly larger text
        settingsBtn.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

        // Enhanced settings button styling with modern gradient
        GradientDrawable settingsBtnBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{BTN_COLOR, Color.parseColor("#1A1A2E")}
        );
        settingsBtnBg.setCornerRadius(18f); // More rounded
        settingsBtnBg.setStroke(2, TEXT_COLOR_ACCENT); // Thicker border
        
        // Add modern elevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settingsBtn.setElevation(3f);
            settingsBtn.setStateListAnimator(null);
        }
        settingsBtn.setBackground(settingsBtnBg);
        settingsBtn.setPadding(16, 10, 16, 10); // Better padding

        LinearLayout.LayoutParams settingsBtnParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        settingsBtnParams.weight = 1.0f;
        settingsBtnParams.setMargins(3, 2, 3, 2); // Better spacing
        settingsBtn.setLayoutParams(settingsBtnParams);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            boolean settingsOpen;

            @Override
            public void onClick(View v) {
                try {
                    settingsOpen = !settingsOpen;
                    if (settingsOpen) {
                        scrollView.removeView(mods);
                        scrollView.addView(mSettings);
                        scrollView.scrollTo(0, 0);
                        settingsBtn.setText("BACK");

                        // Disable menu tabs with hover effect
                        for (int i = 0; i < menuButtons.length; i++) {
                            menuButtons[i].setEnabled(false);
                            menuButtons[i].setTextColor(Color.parseColor("#666666")); // Gray text
                            menuButtons[i].setAlpha(0.5f); // Semi-transparent

                            // Hover effect styling
                            GradientDrawable disabledBg = new GradientDrawable();
                            disabledBg.setCornerRadius(16f);
                            disabledBg.setColor(Color.parseColor("#2A2A2A")); // Darker background
                            disabledBg.setStroke(1, Color.parseColor("#444444")); // Darker border
                            menuButtons[i].setBackground(disabledBg);
                        }
                    } else {
                        scrollView.removeView(mSettings);
                        scrollView.addView(mods);
                        settingsBtn.setText("SETTINGS");

                        // Re-enable menu tabs and restore their state
                        for (int i = 0; i < menuButtons.length; i++) {
                            menuButtons[i].setEnabled(true);
                            menuButtons[i].setAlpha(1.0f); // Full opacity

                            // Reset all buttons to default inactive style first
                            GradientDrawable bg = new GradientDrawable();
                            bg.setCornerRadius(16f);
                            bg.setColor(BTN_COLOR);
                            bg.setStroke(1, BORDER_COLOR);
                            menuButtons[i].setBackground(bg);
                            menuButtons[i].setTextColor(TEXT_COLOR_2);
                        }

                        // Restore correct active menu tab based on tracked index
                        if (menuButtons.length > 0 && currentActiveMenuIndex[0] < menuButtons.length) {
                            GradientDrawable activeBg = new GradientDrawable();
                            activeBg.setCornerRadius(16f);
                            activeBg.setColor(TEXT_COLOR_ACCENT);
                            activeBg.setStroke(2, Color.parseColor("#FFFFFF"));
                            menuButtons[currentActiveMenuIndex[0]].setBackground(activeBg);
                            menuButtons[currentActiveMenuIndex[0]].setTextColor(Color.parseColor("#FFFFFF"));
                        }

                        // Refresh the current menu content
                        mods.removeAllViews();
                        featureList(GetFeatureList(), mods);
                    }
                } catch (IllegalStateException e) {
                }
            }
        });

        //********** Enhanced Premium Minimize Button **********
        Button closeBtn = new Button(context);
        closeBtn.setText("MINIMIZE");
        closeBtn.setTextColor(TEXT_COLOR_2);
        closeBtn.setTextSize(10.5f); // Slightly larger text
        closeBtn.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

        // Enhanced button styling with modern gradient
        GradientDrawable closeBtnBg = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{BTN_COLOR, Color.parseColor("#1A1A2E")}
        );
        closeBtnBg.setCornerRadius(18f); // More rounded
        closeBtnBg.setStroke(2, BORDER_COLOR); // Thicker border
        
        // Add modern elevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeBtn.setElevation(3f);
            closeBtn.setStateListAnimator(null);
        }
        closeBtn.setBackground(closeBtnBg);
        closeBtn.setPadding(16, 10, 16, 10); // Better padding

        LinearLayout.LayoutParams closeBtnParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        closeBtnParams.weight = 1.0f;
        closeBtnParams.setMargins(3, 2, 6, 2); // Better spacing
        closeBtn.setLayoutParams(closeBtnParams);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Add smooth transition animation
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mExpanded.animate().alpha(0f).setDuration(200).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mExpanded.setVisibility(View.GONE);
                            mCollapsed.setAlpha(0f);
                            mCollapsed.setVisibility(View.VISIBLE);
                            mCollapsed.animate().alpha(ICON_ALPHA).setDuration(300).start();
                        }
                    }).start();
                } else {
                    mCollapsed.setVisibility(View.VISIBLE);
                    mCollapsed.setAlpha(ICON_ALPHA);
                    mExpanded.setVisibility(View.GONE);
                }
            }
        });

        // Add buttons to bottom layout
        bottomButtonLayout.addView(hideBtn);
        bottomButtonLayout.addView(settingsBtn);
        bottomButtonLayout.addView(closeBtn);

        //********** Adding view components **********
        mRootContainer.addView(mCollapsed);
        mRootContainer.addView(mExpanded);
        if (IconWebViewData() != null) {
            mCollapsed.addView(wView);
        } else {
            mCollapsed.addView(startimage);
        }
        titleText.addView(leftCircle);
        titleText.addView(title);
        titleText.addView(rightCircle);
        mExpanded.addView(titleText);
        mExpanded.addView(subTitle);
        mExpanded.addView(menuNavigation);
        scrollView.addView(mods);
        mExpanded.addView(scrollView);
        mExpanded.addView(bottomButtonLayout);

        Init(context, title, subTitle);
        
        // Add subtle entrance animations for modern feel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Animate the main container entrance
            mExpanded.setAlpha(0f);
            mExpanded.setScaleX(0.9f);
            mExpanded.setScaleY(0.9f);
            
            // Animate the icon entrance
            startimage.setAlpha(0f);
            startimage.setScaleX(0.8f);
            startimage.setScaleY(0.8f);
            
            // Staggered animations for elegant entrance
            mainHandler.postDelayed(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    startimage.animate()
                        .alpha(ICON_ALPHA)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(400)
                        .start();
                }
            }, 100);
        }
    }

    public void ShowMenu() {
        rootFrame.addView(mRootContainer);

        // Use shared main handler with optimized delay
        mainHandler.postDelayed(new Runnable() {
            boolean viewLoaded = false;
            private static final long CHECK_INTERVAL = 800; // Increased interval to reduce CPU usage

            @Override
            public void run() {
                //If the save preferences is enabled, it will check if game lib is loaded before starting menu
                //Comment the if-else code out except startService if you want to run the app and test preferences
                if (Preferences.loadPref && !IsGameLibLoaded() && !stopChecking) {
                    if (!viewLoaded) {
                        Category(mods, "Save preferences enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods, -100, "Force load menu");
                        viewLoaded = true;
                    }
                    mainHandler.postDelayed(this, CHECK_INTERVAL);
                } else {
                    // Efficiently clear and populate
                    if (mods.getChildCount() > 0) {
                        mods.removeAllViews();
                    }
                    String[] features = GetFeatureList();
                    if (features != null && features.length > 0) {
                        featureList(features, mods);
                    }
                    
                    // Add beautiful entrance animation when content is loaded
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mExpanded.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(500)
                            .start();
                            
                        // Animate menu navigation with delay
                        menuNavigation.setAlpha(0f);
                        menuNavigation.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setStartDelay(200)
                            .start();
                            
                        // Animate bottom buttons with delay
                        bottomButtonLayout.setAlpha(0f);
                        bottomButtonLayout.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setStartDelay(400)
                            .start();
                    }
                }
            }
        }, 300); // Reduced initial delay
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerWindowService() {
        //Variable to check later if the phone supports Draw over other apps permission
        int iparams = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? 2038 : 2002;
        vmParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, iparams, 8, -3);
        //params = new WindowManager.LayoutParams(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW, 8, -3);
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = (WindowManager) getContext.getSystemService(getContext.WINDOW_SERVICE);
        try {
            mWindowManager.addView(rootFrame, vmParams);
            overlayRequired = true;
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
            // Handle exception or use alternative method
            SetWindowManagerWithoutPermission();
        } catch (Exception e) {
            Log.e(TAG, "Exception adding view: " + e.getMessage());
        }
    }

    @SuppressLint("WrongConstant") 
    public void SetWindowManagerWithoutPermission() {
        // For devices that don't have overlay permission - fallback mode
        vmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        try {
            mWindowManager = (WindowManager) getContext.getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.addView(rootFrame, vmParams);
            overlayRequired = false;
        } catch (Exception e) {
            // If still fails, try activity mode
            if (getContext instanceof Activity) {
                try {
                    mWindowManager = ((Activity) getContext).getWindowManager();
                    mWindowManager.addView(rootFrame, vmParams);
                    overlayRequired = false;
                } catch (Exception ex) {
                    Log.e(TAG, "Error adding view in Activity context: " + ex.getMessage());
                    // Handle the exception appropriately, maybe show a toast
                }

            }
        }
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerActivity() {
        vmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                POS_X,//initialX
                POS_Y,//initialy
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = ((Activity) getContext).getWindowManager();
        mWindowManager.addView(rootFrame, vmParams);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX, initialTouchY;
            private int initialX, initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = vmParams.x;
                            initialY = vmParams.y;
                            initialTouchX = motionEvent.getRawX();
                            initialTouchY = motionEvent.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                            int rawY = (int) (motionEvent.getRawY() - initialTouchY);
                            mExpanded.setAlpha(1f);
                            mCollapsed.setVisibility(View.VISIBLE);
                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                try {
                                    collapsedView.setVisibility(View.GONE);
                                    expandedView.setVisibility(View.VISIBLE);
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "NullPointerException in onTouch: " + e.getMessage());
                                }
                            }
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            mExpanded.setAlpha(0.5f);
                            mCollapsed.setAlpha(0.5f);
                            //Calculate the X and Y coordinates of the view.
                            vmParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                            vmParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                            //Update the layout with new X & Y coordinate
                            try {
                                mWindowManager.updateViewLayout(rootFrame, vmParams);
                            } catch (IllegalArgumentException e) {
                                Log.e(TAG, "IllegalArgumentException updating view layout: " + e.getMessage());
                            }
                            return true;
                        default:
                            return false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in onTouch: " + e.getMessage());
                    return false;
                }
            }
        };
    }

    private void featureList(String[] listFT, LinearLayout linearLayout) {
        if (listFT == null || linearLayout == null) {
            Log.w(TAG, "featureList called with null parameters");
            return;
        }

        //Currently looks messy right now. Let me know if you have improvements
        int subFeat = 0;
        LinearLayout llBak = linearLayout;

        // Process features in smaller batches to prevent UI blocking
        final int batchSize = 5;
        final Handler handler = new Handler();

        for (int i = 0; i < listFT.length; i += batchSize) {
            final int startIndex = i;
            final int endIndex = Math.min(i + batchSize, listFT.length);
            final int currentSubFeat = subFeat;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        processFeaturesRange(listFT, linearLayout, llBak, startIndex, endIndex, currentSubFeat);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing features range: " + e.getMessage());
                    }
                }
            });
        }
    }

    private void processFeaturesRange(String[] listFT, LinearLayout linearLayout, LinearLayout llBak, int startIndex, int endIndex, int subFeat) {
        for (int i = startIndex; i < endIndex; i++) {
            int featNum; // Declare featNum here in the correct scope
            boolean switchedOn = false;
            //Log.i("featureList", listFT[i]);
            String feature = listFT[i];
            if (feature.contains("_True")) {
                switchedOn = true;
                feature = feature.replaceFirst("_True", "");
            }

            linearLayout = llBak;
            if (feature.contains("CollapseAdd_")) {
                //if (collapse != null)
                linearLayout = mCollapse;
                feature = feature.replaceFirst("CollapseAdd_", "");
            }
            String[] str = feature.split("_");

            //Assign feature number
            if (TextUtils.isDigitsOnly(str[0]) || str[0].matches("-[0-9]*")) {
                featNum = Integer.parseInt(str[0]);
                feature = feature.replaceFirst(str[0] + "_", "");
                subFeat++;
            } else {
                //Subtract feature number. We don't want to count ButtonLink, Category, RichTextView and RichWebView
                featNum = i - subFeat;
            }
            String[] strSplit = feature.split("_");
            try {
                switch (strSplit[0]) {
                    case "Toggle":
                        Switch(linearLayout, featNum, strSplit[1], switchedOn);
                        break;
                    case "SeekBar":
                        SeekBar(linearLayout, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                        break;
                    case "Button":
                        Button(linearLayout, featNum, strSplit[1]);
                        break;
                    case "ButtonOnOff":
                        ButtonOnOff(linearLayout, featNum, strSplit[1], switchedOn);
                        break;
                    case "Spinner":
                        TextView(linearLayout, strSplit[1]);
                        Spinner(linearLayout, featNum, strSplit[1], strSplit[2]);
                        break;
                    case "InputText":
                        InputText(linearLayout, featNum, strSplit[1]);
                        break;
                    case "InputValue":
                        if (strSplit.length == 3)
                            InputNum(linearLayout, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                        if (strSplit.length == 2)
                            InputNum(linearLayout, featNum, strSplit[1], 0);
                        break;
                    case "CheckBox":
                        CheckBox(linearLayout, featNum, strSplit[1], switchedOn);
                        break;
                    case "RadioButton":
                        RadioButton(linearLayout, featNum, strSplit[1], strSplit[2]);
                        break;
                    case "TextBox":
                        TextBox(linearLayout, featNum, strSplit[1], strSplit.length > 2 ? strSplit[2] : "");
                        break;
                    case "Collapse":
                        Collapse(linearLayout, strSplit[1], switchedOn);
                        subFeat++;
                        break;
                    case "ButtonLink":
                        subFeat++;
                        ButtonLink(linearLayout, strSplit[1], strSplit[2]);
                        break;
                    case "Category":
                        subFeat++;
                        Category(linearLayout, strSplit[1]);
                        break;
                    case "RichTextView":
                        subFeat++;
                        TextView(linearLayout, strSplit[1]);
                        break;
                    case "RichWebView":
                        subFeat++;
                        WebTextView(linearLayout, strSplit[1]);
                        break;
                    default:
                        Log.w(TAG, "Unknown feature type: " + strSplit[0]);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing feature: " + feature + ", " + e.getMessage());
            }
        }
    }

    private void Switch(LinearLayout linLayout, final int featNum, final String featName, boolean swiOn) {
        // Performance optimized switch container with gradient background
        LinearLayout switchContainer = new LinearLayout(getContext);
        switchContainer.setOrientation(LinearLayout.HORIZONTAL);
        switchContainer.setPadding(12, 8, 12, 8);
        switchContainer.setGravity(Gravity.CENTER_VERTICAL);

        // Premium gradient background maintained for visual appeal
        GradientDrawable switchBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{CARD_BG_COLOR, Color.parseColor("#1F1F2E")}
        );
        switchBg.setCornerRadius(8f);
        switchBg.setStroke(1, BORDER_COLOR);
        switchContainer.setBackground(switchBg);

        final Switch switchR = new Switch(getContext);

        // Cached color state list for better performance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                ColorStateList buttonStates = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        },
                        new int[]{ToggleON, ToggleOFF}
                );
                if (switchR.getThumbDrawable() != null)
                    switchR.getThumbDrawable().setTintList(buttonStates);
                if (switchR.getTrackDrawable() != null)
                    switchR.getTrackDrawable().setTintList(buttonStates);
            } catch (Exception ex) {
                Log.e(TAG, "Error setting tint list for switch: " + ex.getMessage());
            }
        }

        switchR.setText(featName);
        switchR.setTextColor(TEXT_COLOR_2);
        switchR.setTextSize(13.0f);
        switchR.setTypeface(null, Typeface.NORMAL);

        LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        switchParams.weight = 1.0f;
        switchR.setLayoutParams(switchParams);
        switchR.setChecked(Preferences.loadPrefBool(featName, featNum, swiOn));
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                try {
                    Preferences.changeFeatureBool(featName, featNum, bool);
                    switch (featNum) {
                        case -1:
                            Preferences.with(switchR.getContext()).writeBoolean(-1, bool);
                            if (!bool) Preferences.with(switchR.getContext()).clear();
                            break;
                        case -3:
                            Preferences.isExpanded = bool;
                            scrollView.setLayoutParams(bool ? scrlLLExpanded : scrlLL);
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in switch checked change: " + e.getMessage());
                }
            }
        });

        switchContainer.addView(switchR);

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        containerParams.setMargins(6, 2, 6, 2);
        switchContainer.setLayoutParams(containerParams);

        linLayout.addView(switchContainer);
    }

    private void TextBox(LinearLayout linLayout, final int featNum, final String featName, final String defaultText) {
        // Premium Read-Only TextBox container with gradient background
        LinearLayout textBoxContainer = new LinearLayout(getContext);
        textBoxContainer.setOrientation(LinearLayout.VERTICAL);
        textBoxContainer.setPadding(12, 10, 12, 10);

        // Premium gradient background
        GradientDrawable textBoxBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{CARD_BG_COLOR, Color.parseColor("#1A1A2E")}
        );
        textBoxBg.setCornerRadius(8f);
        textBoxBg.setStroke(1, BORDER_COLOR);
        textBoxContainer.setBackground(textBoxBg);

        // Title with "READ ONLY" indicator
        TextView titleView = new TextView(getContext);
        titleView.setText(featName + " (Read Only)");
        titleView.setTextColor(TEXT_COLOR_ACCENT);
        titleView.setTextSize(14.0f);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setPadding(0, 0, 0, 8);

        // Main content container
        LinearLayout contentContainer = new LinearLayout(getContext);
        contentContainer.setOrientation(LinearLayout.HORIZONTAL);
        contentContainer.setGravity(Gravity.CENTER_VERTICAL);

        // Text display area with scrollable content - READ ONLY
        final TextView textDisplay = new TextView(getContext);

        // Get content from C++ via JNI
        try {
            String cppContent = getTextBoxContent(featNum);
            textDisplay.setText(cppContent.isEmpty() ? defaultText : cppContent);
        } catch (Exception e) {
            textDisplay.setText(defaultText);
        }

        textDisplay.setTextColor(TEXT_COLOR_2);
        textDisplay.setTextSize(12.0f);
        textDisplay.setPadding(12, 8, 8, 8);
        textDisplay.setMaxLines(5); // More lines for better content display
        textDisplay.setEllipsize(TextUtils.TruncateAt.END);

        // Read-only styling with different background
        GradientDrawable textBg = new GradientDrawable();
        textBg.setCornerRadius(6f);
        textBg.setColor(Color.parseColor("#0A0A14"));
        textBg.setStroke(1, Color.parseColor("#444444")); // Gray border for read-only
        textDisplay.setBackground(textBg);

        // Make it non-focusable and non-clickable for editing
        textDisplay.setFocusable(false);
        textDisplay.setClickable(false);
        textDisplay.setLongClickable(false);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        textParams.weight = 1.0f;
        textParams.setMargins(0, 0, 8, 0);
        textDisplay.setLayoutParams(textParams);


        // Copy button with premium styling
        Button copyButton = new Button(getContext);
        copyButton.setText("📋");
        copyButton.setTextSize(14.0f);
        copyButton.setTextColor(TEXT_COLOR_2);
        copyButton.setPadding(12, 8, 12, 8);

        GradientDrawable copyBtnBg = new GradientDrawable();
        copyBtnBg.setCornerRadius(6f);
        copyBtnBg.setColor(BTN_COLOR);
        copyBtnBg.setStroke(1, TEXT_COLOR_ACCENT);
        copyButton.setBackground(copyBtnBg);

        LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        copyParams.setMargins(0, 0, 0, 0);
        copyButton.setLayoutParams(copyParams);

        // Copy functionality - ONLY copy, no editing
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager clipboard = (ClipboardManager) getContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(featName, textDisplay.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext, "Text copied to clipboard!", Toast.LENGTH_SHORT).show();

                    // Visual feedback with shared handler
                    copyButton.setText("✓");
                    mainHandler.postDelayed(() -> copyButton.setText("📋"), 1000);
                } catch (Exception e) {
                    Log.e(TAG, "Error copying text to clipboard: " + e.getMessage());
                }
            }
        });

        contentContainer.addView(textDisplay);
        contentContainer.addView(copyButton);

        textBoxContainer.addView(titleView);
        textBoxContainer.addView(contentContainer);

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        containerParams.setMargins(6, 4, 6, 4);
        textBoxContainer.setLayoutParams(containerParams);

        linLayout.addView(textBoxContainer);
    }

    private void SeekBar(LinearLayout linLayout, final int featNum, final String featName, final int min, int max) {
        int loadedProg = Preferences.loadPrefInt(featName, featNum);

        // Optimized seekbar container - reduced styling complexity
        LinearLayout linearLayout = new LinearLayout(getContext);
        linearLayout.setPadding(10, 6, 10, 6);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        // Simplified background for better performance
        linearLayout.setBackgroundColor(CARD_BG_COLOR);

        final TextView textView = new TextView(getContext);
        textView.setText(featName + ": " + ((loadedProg == 0) ? min : loadedProg));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTextSize(13.0f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 0, 0, 6);

        SeekBar seekBar = new SeekBar(getContext);
        seekBar.setPadding(15, 8, 15, 8);
        seekBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            seekBar.setMin(min);
        seekBar.setProgress((loadedProg == 0) ? min : loadedProg);

        // Optimized seekbar styling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.setProgressTintList(ColorStateList.valueOf(SeekBarProgressColor));
            seekBar.setThumbTintList(ColorStateList.valueOf(SeekBarColor));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                int value = i < min ? min : i;
                if (z) { // Only update if user initiated
                    try {
                        Preferences.changeFeatureInt(featName, featNum, value);
                        textView.setText(featName + ": " + value);
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating seekbar value: " + e.getMessage());
                    }
                }
            }
        });
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);

        linLayout.addView(linearLayout);
    }

    private void Button(LinearLayout linLayout, final int featNum, final String featName) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.setMargins(8, 6, 8, 6);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false);
        button.setText(Html.fromHtml(featName));
        button.setTextSize(14.0f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setPadding(16, 12, 16, 12);

        // Premium button styling
        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setCornerRadius(8f);
        buttonBg.setColor(BTN_COLOR);
        buttonBg.setStroke(1, BORDER_COLOR);
        button.setBackground(buttonBg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(2f);
            button.setStateListAnimator(null); // Remove default animation
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    switch (featNum) {

                        case -6:
                            scrollView.removeView(mSettings);
                            scrollView.addView(mods);
                            break;
                        case -100:
                            stopChecking = true;
                            break;
                    }
                    Preferences.changeFeatureInt(featName, featNum, 0);
                } catch (Exception e) {
                    Log.e(TAG, "Error handling button click: " + e.getMessage());
                }
            }
        });

        linLayout.addView(button);
    }

    private void ButtonLink(LinearLayout linLayout, final String featName, final String url) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setAllCaps(false); //Disable caps to support html
        button.setTextColor(TEXT_COLOR_2);
        button.setText(Html.fromHtml(featName));
        button.setBackgroundColor(BTN_COLOR);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    getContext.startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening URL: " + e.getMessage());
                    Toast.makeText(getContext, "Could not open link", Toast.LENGTH_SHORT).show();
                }
            }
        });
        linLayout.addView(button);
    }

    private void ButtonOnOff(LinearLayout linLayout, final int featNum, String featName, boolean switchedOn) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html

        final String finalfeatName = featName.replace("OnOff_", "");
        boolean isOn = Preferences.loadPrefBool(featName, featNum, switchedOn);
        if (isOn) {
            button.setText(Html.fromHtml(finalfeatName + ": ON"));
            button.setBackgroundColor(BtnON);
            isOn = false;
        } else {
            button.setText(Html.fromHtml(finalfeatName + ": OFF"));
            button.setBackgroundColor(BtnOFF);
            isOn = true;
        }
        final boolean finalIsOn = isOn;
        button.setOnClickListener(new View.OnClickListener() {
            boolean isOn = finalIsOn;

            public void onClick(View v) {
                try {
                    Preferences.changeFeatureBool(finalfeatName, featNum, isOn);
                    //Log.d(TAG, finalfeatName + " " + featNum + " " + isActive2);
                    if (isOn) {
                        button.setText(Html.fromHtml(finalfeatName + ": ON"));
                        button.setBackgroundColor(BtnON);
                        isOn = false;
                    } else {
                        button.setText(Html.fromHtml(finalfeatName + ": OFF"));
                        button.setBackgroundColor(BtnOFF);
                        isOn = true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling button on/off click: " + e.getMessage());
                }
            }
        });
        linLayout.addView(button);
    }

    private void Spinner(LinearLayout linLayout, final int featNum, final String featName, final String list) {
        Log.d(TAG, "spinner " + featNum + " " + featName + " " + list);
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        // Create another LinearLayout as a workaround to use it as a background
        // to keep the down arrow symbol. No arrow symbol if setBackgroundColor set
        LinearLayout linearLayout2 = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams2.setMargins(7, 2, 7, 2);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setBackgroundColor(BTN_COLOR);
        linearLayout2.setLayoutParams(layoutParams2);

        final Spinner spinner = new Spinner(getContext, Spinner.MODE_DROPDOWN);
        spinner.setLayoutParams(layoutParams2);
        spinner.getBackground().setColorFilter(1, PorterDuff.Mode.SRC_ATOP); //trick to show white down arrow color
        //Creating the ArrayAdapter instance having the list
        ArrayAdapter aa = new ArrayAdapter(getContext, android.R.layout.simple_spinner_dropdown_item, lists);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner'
        spinner.setAdapter(aa);
        spinner.setSelection(Preferences.loadPrefInt(featName, featNum));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    Preferences.changeFeatureInt(spinner.getSelectedItem().toString(), featNum, position);
                    ((TextView) parentView.getChildAt(0)).setTextColor(TEXT_COLOR_2);
                } catch (Exception e) {
                    Log.e(TAG, "Error selecting spinner item: " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        linearLayout2.addView(spinner);
        linLayout.addView(linearLayout2);
    }

    private void InputNum(LinearLayout linLayout, final int featNum, final String featName, final int maxValue) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);

        final Button button = new Button(getContext);
        int num = Preferences.loadPrefInt(featName, featNum);
        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + ((num == 0) ? 1 : num) + "</font>"));
        button.setAllCaps(false);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(BTN_COLOR);
        button.setTextColor(TEXT_COLOR_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext);
                final EditText editText = new EditText(getContext);
                if (maxValue != 0)
                    editText.setHint("Max value: " + maxValue);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(10);
                editText.setFilters(FilterArray);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                });
                editText.requestFocus();

                alertName.setTitle("Input number");
                alertName.setView(editText);
                LinearLayout layoutName = new LinearLayout(getContext);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editText); // displays the user input bar
                alertName.setView(layoutName);

                alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int num;
                        try {
                            num = Integer.parseInt(TextUtils.isEmpty(editText.getText().toString()) ? "0" : editText.getText().toString());
                            if (maxValue != 0 && num >= maxValue)
                                num = maxValue;
                        } catch (NumberFormatException ex) {
                            if (maxValue != 0)
                                num = maxValue;
                            else
                                num = 2147483640;
                        }

                        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + num + "</font>"));
                        Preferences.changeFeatureInt(featName, featNum, num);

                        editText.setFocusable(false);
                    }
                });

                alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // dialog.cancel(); // closes dialog
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                AlertDialog dialog = alertName.create(); // display the dialog
                try {
                    if (overlayRequired) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        } else {
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                        }

                    }
                    dialog.show();
                } catch (WindowManager.BadTokenException e) {
                    Log.e(TAG, "BadTokenException: " + e.getMessage());
                    //The activity is no longer in the foreground
                }
            }
        });

        linearLayout.addView(button);
        linLayout.addView(linearLayout);
    }

    private void InputText(LinearLayout linLayout, final int featNum, final String featName) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);

        final Button button = new Button(getContext);

        String string = Preferences.loadPrefString(featName, featNum);
        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + string + "</font>"));

        button.setAllCaps(false);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(BTN_COLOR);
        button.setTextColor(TEXT_COLOR_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext);

                final EditText editText = new EditText(getContext);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                });
                editText.requestFocus();

                alertName.setTitle("Input text");
                alertName.setView(editText);
                LinearLayout layoutName = new LinearLayout(getContext);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editText); // displays the user input bar
                alertName.setView(layoutName);

                alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = editText.getText().toString();
                        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + str + "</font>"));
                        Preferences.changeFeatureString(featName, featNum, str);
                        editText.setFocusable(false);
                    }
                });

                alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //dialog.cancel(); // closes dialog
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                AlertDialog dialog = alertName.create(); // display the dialog
                try {
                    if (overlayRequired) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        } else {
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                        }

                    }
                    dialog.show();
                } catch (WindowManager.BadTokenException e) {
                    Log.e(TAG, "BadTokenException: " + e.getMessage());
                    //The activity is no longer in the foreground
                }
            }
        });

        linearLayout.addView(button);
        linLayout.addView(linearLayout);
    }

    private void CheckBox(LinearLayout linLayout, final int featNum, final String featName, boolean switchedOn) {
        // Optimized checkbox container - simplified styling
        LinearLayout checkboxContainer = new LinearLayout(getContext);
        checkboxContainer.setOrientation(LinearLayout.HORIZONTAL);
        checkboxContainer.setPadding(10, 6, 10, 6);
        checkboxContainer.setGravity(Gravity.CENTER_VERTICAL);

        // Simplified background for performance
        checkboxContainer.setBackgroundColor(CARD_BG_COLOR);

        final CheckBox checkBox = new CheckBox(getContext);
        checkBox.setText(featName);
        checkBox.setTextColor(TEXT_COLOR_2);
        checkBox.setTextSize(13.0f);
        checkBox.setTypeface(null, Typeface.NORMAL);

        // Simplified checkbox styling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
        }
        checkBox.setChecked(Preferences.loadPrefBool(featName, featNum, switchedOn));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                } catch (Exception e) {
                    Log.e(TAG, "Error updating checkbox state: " + e.getMessage());
                }
            }
        });

        checkboxContainer.addView(checkBox);

        // Reduced margins for performance
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        containerParams.setMargins(6, 2, 6, 2);
        checkboxContainer.setLayoutParams(containerParams);

        linLayout.addView(checkboxContainer);
    }

    private void RadioButton(LinearLayout linLayout, final int featNum, String featName, final String list) {
        //Credit: LoraZalora
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        final TextView textView = new TextView(getContext);
        textView.setText(featName + ":");
        textView.setTextColor(TEXT_COLOR_2);

        final RadioGroup radioGroup = new RadioGroup(getContext);
        radioGroup.setPadding(10, 5, 10, 5);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.addView(textView);

        for (int i = 0; i < lists.size(); i++) {
            final RadioButton Radioo = new RadioButton(getContext);
            final String finalfeatName = featName, radioName = lists.get(i);
            View.OnClickListener first_radio_listener = new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        textView.setText(Html.fromHtml(finalfeatName + ": <font color='" + NumberTxtColor + "'>" + radioName));
                        Preferences.changeFeatureInt(finalfeatName, featNum, radioGroup.indexOfChild(Radioo));
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling radio button click: " + e.getMessage());
                    }
                }
            };
            System.out.println(lists.get(i));
            Radioo.setText(lists.get(i));
            Radioo.setTextColor(Color.LTGRAY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Radioo.setButtonTintList(ColorStateList.valueOf(RadioColor));
            Radioo.setOnClickListener(first_radio_listener);
            radioGroup.addView(Radioo);
        }

        int index = Preferences.loadPrefInt(featName, featNum);
        if (index > 0) { //Preventing it to get an index less than 1. below 1 = null = crash
            textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + lists.get(index - 1)));
            ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        }
        linLayout.addView(radioGroup);
    }

    private void Collapse(LinearLayout linLayout, final String text, final boolean expanded) {
        LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParamsLL.setMargins(0, 5, 0, 0);

        LinearLayout collapse = new LinearLayout(getContext);
        collapse.setLayoutParams(layoutParamsLL);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout collapseSub = new LinearLayout(getContext);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(0, 5, 0, 5);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(Color.parseColor("#222D38"));
        collapseSub.setVisibility(View.GONE);
        mCollapse = collapseSub;

        final TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText("▽ " + text + " ▽");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 20, 0, 20);

        if (expanded) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setText("△ " + text + " △");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = expanded;

            @Override
            public void onClick(View v) {

                boolean z = !isChecked;
                isChecked = z;
                if (z) {
                    collapseSub.setVisibility(View.VISIBLE);
                    textView.setText("△ " + text + " △");
                    return;
                }
                collapseSub.setVisibility(View.GONE);
                textView.setText("▽ " + text + " ▽");
            }
        });
        collapse.addView(textView);
        collapse.addView(collapseSub);
        linLayout.addView(collapse);
    }

    private void Category(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_ACCENT);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(16.0f);
        textView.setPadding(16, 12, 16, 12);

        // Premium category styling
        GradientDrawable categoryBg = new GradientDrawable();
        categoryBg.setCornerRadius(6f);
        categoryBg.setColor(CategoryBG);
        categoryBg.setStroke(1, BORDER_COLOR);
        textView.setBackground(categoryBg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setElevation(1f);
            textView.setLetterSpacing(0.1f);
        }

        // Add margin
        LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        categoryParams.setMargins(8, 8, 8, 4);
        textView.setLayoutParams(categoryParams);

        linLayout.addView(textView);
    }

    private void TextView(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setPadding(10, 5, 10, 5);
        linLayout.addView(textView);
    }

    private void WebTextView(LinearLayout linLayout, String text) {
        WebView wView = new WebView(getContext);
        wView.loadData(text, "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setPadding(0, 5, 0, 5);
        linLayout.addView(wView);
    }

    private boolean isViewCollapsed() {
        return rootFrame == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    //For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) i, getContext.getResources().getDisplayMetrics());
    }

    public void setVisibility(int view) {
        if (rootFrame != null) {
            rootFrame.setVisibility(view);
        }
    }
    
    /**
     * Get root frame for visibility checks
     */
    public FrameLayout getRootFrame() {
        return rootFrame;
    }

    public void onDestroy() {
        try {
            // Clean up handlers to prevent memory leaks
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
            
            // Clean up root frame
            if (rootFrame != null) {
                try {
                    if (mWindowManager != null) {
                        mWindowManager.removeView(rootFrame);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "View not attached to window manager", e);
                } catch (Exception e) {
                    Log.e(TAG, "Error removing view from window manager", e);
                }
                rootFrame = null;
            }
            
            // Clean up menu components
            if (menu != null) {
                menu = null;
            }
            
            // Clean up scroll view
            if (scrollView != null) {
                scrollView = null;
            }
            
            // Clean up layouts
            mods = null;
            mExpanded = null;
            mCollapsed = null;
            mRootContainer = null;
            
            Log.d(TAG, "Menu destroyed and cleaned up");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during menu cleanup", e);
        }
    }
}