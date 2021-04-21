package net.kdt.pojavlaunch.customcontrols;

import android.graphics.Color;
import android.util.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.utils.*;
import net.objecthunter.exp4j.*;
import org.lwjgl.glfw.*;

public class ControlData implements Cloneable
{

    public static final int SPECIALBTN_KEYBOARD = -1;
    public static final int SPECIALBTN_TOGGLECTRL = -2;
    public static final int SPECIALBTN_MOUSEPRI = -3;
    public static final int SPECIALBTN_MOUSESEC = -4;
    public static final int SPECIALBTN_VIRTUALMOUSE = -5;
    public static final int SPECIALBTN_MOUSEMID = -6;
    public static final int SPECIALBTN_SCROLLUP = -7;
    public static final int SPECIALBTN_SCROLLDOWN = -8;
    
    private static ControlData[] SPECIAL_BUTTONS;
    private static String[] SPECIAL_BUTTON_NAME_ARRAY;

    // Internal usage only
    public boolean isHideable;
    
    /**
     * Both fields below are dynamic position data, auto updates
     * X and Y position, unlike the original one which uses fixed
     * position, so it does not provide auto-location when a control
     * is made on a small device, then import the control to a
     * bigger device or vice versa.
     */
    public String dynamicX, dynamicY;
    public boolean isDynamicBtn, isToggle, passThruEnabled;
    
    public static ControlData[] getSpecialButtons(){
        if (SPECIAL_BUTTONS == null) {
            ControlData[] specialButtons = new ControlData[]{
                new ControlData("Keyboard", new int[]{SPECIALBTN_KEYBOARD}, "${margin} * 3 + ${width} * 2", "${margin}", false),
                new ControlData("GUI", new int[]{SPECIALBTN_TOGGLECTRL}, "${margin}", "${bottom} - ${margin}"),
                new ControlData("PRI", new int[]{SPECIALBTN_MOUSEPRI}, "${margin}", "${screen_height} - ${margin} * 3 - ${height} * 3"),
                new ControlData("SEC", new int[]{SPECIALBTN_MOUSESEC}, "${margin} * 3 + ${width} * 2", "${screen_height} - ${margin} * 3 - ${height} * 3"),
                new ControlData("Mouse", new int[]{SPECIALBTN_VIRTUALMOUSE}, "${right}", "${margin}", false),

                new ControlData("MID", new int[]{SPECIALBTN_MOUSEMID}, "${margin}", "${margin}"),
                new ControlData("SCROLLUP", new int[]{SPECIALBTN_SCROLLUP}, "${margin}", "${margin}"),
                new ControlData("SCROLLDOWN", new int[]{SPECIALBTN_SCROLLDOWN}, "${margin}", "${margin}")
            };
            SPECIAL_BUTTONS = specialButtons;
        }

        return SPECIAL_BUTTONS;
    }

    public static String[] buildSpecialButtonArray() {
        if (SPECIAL_BUTTON_NAME_ARRAY == null) {
            List<String> nameList = new ArrayList<String>();
            for (ControlData btn : getSpecialButtons()) {
                nameList.add(btn.name);
            }
            SPECIAL_BUTTON_NAME_ARRAY = nameList.toArray(new String[0]);
        }

        return SPECIAL_BUTTON_NAME_ARRAY;
    }

    public String name;
    public float x;
    public float y;
    public float width;
    public float height;
    public int[] keycodes; //Should store up to 4 keys
    public float opacity; //Alpha value from 0 to 1;
    public int bgColor;
    public int strokeColor;
    public int strokeWidth;
    public float cornerRadius;

    @Deprecated
    public boolean hidden;
    public boolean holdCtrl;
    public boolean holdAlt;
    public boolean holdShift;
    public Object specialButtonListener;

    public ControlData() {
        this("", new int[]{LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN}, 0, 0);
    }

    public ControlData(String name, int[] keycodes) {
        this(name, keycodes, 0, 0);
    }

    public ControlData(String name, int[] keycodes, float x, float y) {
        this(name, keycodes, x, y, Tools.dpToPx(50), Tools.dpToPx(50));
    }

    public ControlData(android.content.Context ctx, int resId, int[] keycodes, float x, float y, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycodes, x, y, isSquare);
    }

    public ControlData(String name, int[] keycodes, float x, float y, boolean isSquare) {
        this(name, keycodes, x, y, isSquare ? Tools.dpToPx(50) : Tools.dpToPx(80), isSquare ? Tools.dpToPx(50) : Tools.dpToPx(30));
    }

    public ControlData(String name, int[] keycodes, float x, float y, float width, float height) {
        this(name, keycodes, Float.toString(x), Float.toString(y), width, height, false);
        this.isDynamicBtn = false;
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY) {
        this(name, keycodes, dynamicX, dynamicY, Tools.dpToPx(50), Tools.dpToPx(50), false);
    }

    public ControlData(android.content.Context ctx, int resId, int[] keycodes, String dynamicX, String dynamicY, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycodes, dynamicX, dynamicY, isSquare);
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, boolean isSquare) {
        this(name, keycodes, dynamicX, dynamicY, isSquare ? Tools.dpToPx(50) : Tools.dpToPx(80), isSquare ? Tools.dpToPx(50) : Tools.dpToPx(30), false);
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, float width, float height, boolean isToggle){
        this(name, keycodes, dynamicX, dynamicY, width, height, isToggle, 1,0x4D000000, 0xFFFFFFFF,0,Tools.dpToPx(0));
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, float width, float height, boolean isToggle, float opacity, int bgColor, int strokeColor, int strokeWidth, float cornerRadius) {
        this.name = name;
        this.keycodes = inflateKeycodeArray(keycodes);
        this.dynamicX = dynamicX;
        this.dynamicY = dynamicY;
        this.width = width;
        this.height = height;
        this.isDynamicBtn = true;
        this.isToggle = isToggle;
        this.opacity = opacity;
        this.bgColor = bgColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.cornerRadius = cornerRadius;
        update();
    }
    
    public void execute(BaseMainActivity act, boolean isDown) {
        for(int keycode : keycodes){
            act.sendKeyPress(keycode, 0, isDown);
        }
    }

    public ControlData clone() {
        if (this instanceof ControlData) {
            return new ControlData(name, keycodes, ((ControlData) this).dynamicX, ((ControlData) this).dynamicY, width, height, isToggle, opacity, bgColor, strokeColor,strokeWidth, cornerRadius);
        } else {
            return new ControlData(name, keycodes, x, y, width, height);
        }
    }
    
    public float insertDynamicPos(String dynamicPos) {
        // Values in the map below may be always changed
        Map<String, String> keyValueMap = new ArrayMap<>();
        keyValueMap.put("top", "0");
        keyValueMap.put("left", "0");
        keyValueMap.put("right", Float.toString(CallbackBridge.physicalWidth - width));
        keyValueMap.put("bottom", Float.toString(CallbackBridge.physicalHeight - height));
        keyValueMap.put("width", Float.toString(width));
        keyValueMap.put("height", Float.toString(height));
        keyValueMap.put("screen_width", Integer.toString(CallbackBridge.physicalWidth));
        keyValueMap.put("screen_height", Integer.toString(CallbackBridge.physicalHeight));
        keyValueMap.put("margin", Integer.toString((int) Tools.dpToPx(2)));
        
        // Insert value to ${variable}
        String insertedPos = JSONUtils.insertSingleJSONValue(dynamicPos, keyValueMap);
        
        // Calculate, because the dynamic position contains some math equations
        return calculate(insertedPos);
    }
    
    public void update() {
        if(SPECIAL_BUTTONS != null){
            for(int keycode : keycodes){
                for (ControlData data : getSpecialButtons()) {
                    if (keycode == data.keycodes[0]) {
                        specialButtonListener = data.specialButtonListener;
                    }
                }
            }
        }

        if (dynamicX == null) {
            dynamicX = Float.toString(x);
        }
        if (dynamicY == null) {
            dynamicY = Float.toString(y);
        }
        
        x = insertDynamicPos(dynamicX);
        y = insertDynamicPos(dynamicY);
    }

    private static float calculate(String math) {
        return (float) new ExpressionBuilder(math).build().evaluate();
    }

    private static int[] inflateKeycodeArray(int[] keycodes){
        int[] inflatedArray = new int[4];
        int i;
        for(i=0; i<keycodes.length; ++i){
            inflatedArray[i] = keycodes[i];
        }
        for(;i<4;++i){
            inflatedArray[i] = LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN;
        }
        return inflatedArray;
    }
}
