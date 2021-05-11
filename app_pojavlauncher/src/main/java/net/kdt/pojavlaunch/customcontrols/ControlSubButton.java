package net.kdt.pojavlaunch.customcontrols;

import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

public class ControlSubButton extends ControlButton {

    public ControlDrawer parentDrawer;

    public ControlSubButton(ControlLayout layout, ControlData properties, ControlDrawer parentDrawer) {
        super(layout, properties);
        this.parentDrawer = parentDrawer;

        //Delayed to let the button inflate first
        new Handler(Looper.getMainLooper()).postDelayed(() -> setVisibility(parentDrawer.areButtonsVisible ? VISIBLE : GONE), 0);

        filterProperties();
    }

    private void filterProperties(){
        mProperties.setHeight(parentDrawer.getProperties().getHeight());
        mProperties.setWidth(parentDrawer.getProperties().getWidth());
        mProperties.isDynamicBtn = false;

        setProperties(mProperties, false);
    }




    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if(parentDrawer != null){
            params.width = (int)parentDrawer.mProperties.getWidth();
            params.height = (int)parentDrawer.mProperties.getHeight();
        }

        super.setLayoutParams(params);

    }
}
