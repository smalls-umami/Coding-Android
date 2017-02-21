package net.coding.program.setting;

import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.ImageLoadTool;
import net.coding.program.common.base.MyJsonResponse;
import net.coding.program.common.network.MyAsyncHttpClient;
import net.coding.program.common.ui.BackActivity;
import net.coding.program.common.widget.CircleImageView;
import net.coding.program.model.EnterpriseAccount;
import net.coding.program.model.EnterpriseInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_enterprise_account)
@OptionsMenu(R.menu.enterprise_account_menu)
public class EnterpriseAccountActivity extends BackActivity {

    @ViewById
    TextView accountState;
    @ViewById
    CircleImageView companyIcon;
    @ViewById
    TextView companyName;
    @ViewById
    Toolbar toolbar;

    @AfterViews
    void initEnterpriseAccountActivity() {
        initToolbar();
        ImageLoader.getInstance().displayImage(EnterpriseInfo.instance().getAvatar(), companyIcon, ImageLoadTool.options);
        companyName.setText(EnterpriseInfo.instance().getName());

        String host = String.format("%s/enterprise/%s", Global.HOST_API, EnterpriseInfo.instance().getGlobalkey());
        MyAsyncHttpClient.get(this, host, new MyJsonResponse(this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);

                EnterpriseAccount account = new EnterpriseAccount(response.optJSONObject("data"));
                Spanned countString;
                if (account.remaindays < 0) {
                    countString = Global.createColorHtml("", "您的服务已过期，请订购后使用", "", "#F56061");
                } else if (account.remaindays == 1) {
                    countString = Global.createColorHtml("", "您的服务预计于明天暂停，请尽快订购", "", "#F56061");
                } else if (account.trial) {
                    countString = Global.createColorHtml("试用期剩余 ", String.valueOf(account.remaindays), " 天", "#32BE77");
                } else {
                    countString = Global.createColorHtml("账户余额：", account.balance, " 元", "#FB8638");
                }
                accountState.setText(countString);
            }

            @Override
            public void onMyFailure(JSONObject response) {
                super.onMyFailure(response);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        if(Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Click
    void itemProject() {
        ManageProjectListActivity_.intent(this).start();
    }

    @Click
    void itemMember() {
        ManageMemberActivity_.intent(this).start();
    }

    @OptionsItem
    void action_setting(){
        EnterpriseSettingActivity_.intent(this).start();
    }
}
