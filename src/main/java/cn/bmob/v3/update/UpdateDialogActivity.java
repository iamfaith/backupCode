package cn.bmob.v3.update;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.bmob.v3.b.From;
import cn.bmob.v3.b.of;
import cn.bmob.v3.update.a.This;
import java.io.File;

public class UpdateDialogActivity extends Activity implements OnClickListener {
    private int B;
    private int C;
    UpdateResponse Code;
    private boolean F = false;
    private int I;
    private File S = null;
    private int V = 5;
    private int Z;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(of.Code((Context) this).V("bmob_update_dialog"));
        this.Code = (UpdateResponse) getIntent().getExtras().getSerializable("response");
        String string = getIntent().getExtras().getString("file");
        this.F = getIntent().getExtras().getBoolean("showCheckBox", false);
        boolean z = string != null;
        if (z) {
            this.S = new File(string);
        }
        int Code = of.Code((Context) this).Code("bmob_update_content");
        int Code2 = of.Code((Context) this).Code("bmob_update_wifi_indicator");
        this.I = of.Code((Context) this).Code("bmob_update_id_ok");
        this.Z = of.Code((Context) this).Code("bmob_update_id_cancel");
        of.Code((Context) this).Code("bmob_update_id_ignore");
        this.B = of.Code((Context) this).Code("bmob_update_id_close");
        this.C = of.Code((Context) this).Code("bmob_update_id_check");
        if (!this.Code.isforce.booleanValue() || This.B() || This.I()) {
            findViewById(this.B).setVisibility(8);
            findViewById(this.Z).setVisibility(0);
        } else {
            findViewById(this.B).setVisibility(8);
            findViewById(this.Z).setVisibility(8);
        }
        findViewById(this.I).setOnClickListener(this);
        findViewById(this.Z).setOnClickListener(this);
        findViewById(this.B).setOnClickListener(this);
        ((CheckBox) findViewById(this.C)).setOnCheckedChangeListener(new OnCheckedChangeListener(this) {
            private /* synthetic */ UpdateDialogActivity Code;

            {
                this.Code = r1;
            }

            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    BmobUpdateAgent.add2IgnoreVersion(String.valueOf(this.Code.Code.version_i));
                } else if (BmobUpdateAgent.isIgnored(String.valueOf(this.Code.Code.version_i))) {
                    BmobUpdateAgent.deleteIgnoreVersion(String.valueOf(this.Code.Code.version_i));
                }
            }
        });
        if (Code2 > 0) {
            findViewById(Code2).setVisibility(From.V(this) ? 8 : 0);
        }
        if (!this.F || this.Code.isforce.booleanValue()) {
            findViewById(this.C).setVisibility(8);
        } else {
            findViewById(this.C).setVisibility(0);
        }
        CharSequence updateInfo = this.Code.getUpdateInfo(this, z);
        TextView textView = (TextView) findViewById(Code);
        textView.requestFocus();
        textView.setText(updateInfo);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.Code.isforce.booleanValue()) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View v) {
        if (this.I == v.getId()) {
            this.V = 6;
        } else if (this.Z == v.getId()) {
            this.V = 7;
        } else if (this.B == v.getId()) {
            this.V = 8;
        }
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        BmobUpdateAgent.Code(this.V, (Context) this, this.Code, this.S);
    }
}
