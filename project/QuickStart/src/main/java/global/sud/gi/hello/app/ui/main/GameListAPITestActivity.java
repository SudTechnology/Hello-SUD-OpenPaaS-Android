package global.sud.gi.hello.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.LanguageUtils;
import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.listener.SUDOPGameListCallback;
import global.sud.op.runtime.core.listener.SUDOPSelectionListCallback;
import global.sud.op.runtime.core.listener.SUDOPInitSDKListener;
import global.sud.op.runtime.core.model.SUDOPGameListItem;
import global.sud.op.runtime.core.model.SUDOPGameListParams;
import global.sud.op.runtime.core.model.SUDOPGameListResult;
import global.sud.op.runtime.core.model.SUDOPSelectionListItem;
import global.sud.op.runtime.core.model.SUDOPSelectionListParams;
import global.sud.op.runtime.core.model.SUDOPSelectionListResult;

public class GameListAPITestActivity extends BaseActivity {

    public static final String EXTRA_SELECTION_MODE = "selection_mode";
    public static final String EXTRA_SELECTION_LIST_MODE = "selection_list_mode";
    public static final String EXTRA_SELECTION_ID = "selection_id";
    private static final String TAG = "SUDOPGameListTest";

    private final List<String> records = new ArrayList<>();
    private final List<SUDOPSelectionListItem> selections = new ArrayList<>();
    private boolean selectionListMode;
    private EditText pageNoField;
    private EditText pageSizeField;
    private EditText selectionIdField;
    private boolean selectionMode;
    private Button requestButton;
    private Button previousButton;
    private Button nextButton;
    private TextView statusView;
    private GameListAdapter adapter;
    private int total;
    private boolean requestInProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_list_api_test;
    }

    @Override
    protected void initWidget() {
        pageNoField = findViewById(R.id.et_page_no);
        pageSizeField = findViewById(R.id.et_page_size);
        requestButton = findViewById(R.id.btn_request_game_list);
        previousButton = findViewById(R.id.btn_previous_page);
        nextButton = findViewById(R.id.btn_next_page);
        statusView = findViewById(R.id.tv_request_status);
        selectionListMode = getIntent().getBooleanExtra(EXTRA_SELECTION_LIST_MODE, false);
        selectionMode = !selectionListMode && getIntent().getBooleanExtra(EXTRA_SELECTION_MODE, false);
        selectionIdField = findViewById(R.id.sud_op_selection_id);
        if (selectionMode) {
            selectionIdField.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.sud_op_game_list_title)).setText(R.string.sud_op_selection_game_list);
            statusView.setText(R.string.sud_op_selection_game_list_hint);
            selectionIdField.setText(getIntent().getStringExtra(EXTRA_SELECTION_ID));
        } else if (selectionListMode) {
            ((TextView) findViewById(R.id.sud_op_game_list_title)).setText(R.string.sud_op_selection_list);
            requestButton.setText(R.string.sud_op_selection_list);
            statusView.setText(R.string.sud_op_selection_list_hint);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_game_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameListAdapter(records);
        recyclerView.setAdapter(adapter);
        updatePageButtons();
    }

    @Override
    protected void setListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        requestButton.setOnClickListener(v -> requestGameList());
        adapter.setOnItemClickListener((listAdapter, view, position) -> {
            if (!selectionListMode || requestInProgress || position >= selections.size()) {
                return;
            }
            Intent intent = new Intent(this, GameListAPITestActivity.class);
            intent.putExtra(EXTRA_SELECTION_MODE, true);
            intent.putExtra(EXTRA_SELECTION_ID, selections.get(position).selectionId);
            startActivity(intent);
        });
        previousButton.setOnClickListener(v -> {
            pageNoField.setText(String.valueOf(Math.max(0, getPageNo() - 1)));
            requestGameList();
        });
        nextButton.setOnClickListener(v -> {
            int pageNo = getPageNo();
            if (pageNo < Integer.MAX_VALUE) {
                pageNoField.setText(String.valueOf(pageNo + 1));
            }
            requestGameList();
        });
    }

    private void requestGameList() {
        if (requestInProgress) {
            return;
        }
        hideKeyboard();
        Long selectionId = null;
        if (selectionMode) {
            try {
                selectionId = Long.parseLong(selectionIdField.getText().toString().trim());
                if (selectionId < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                selectionIdField.setError(getString(R.string.sud_op_selection_id_invalid));
                return;
            }
        }
        Long requestedSelectionId = selectionId;
        int pageNo = getPageNo();
        int pageSize = getPageSize();
        pageNoField.setText(String.valueOf(pageNo));
        pageSizeField.setText(String.valueOf(pageSize));
        setRequestInProgress(true);

        SUDOP.initSDK(getApplicationContext(), AppConfig.SudGIP_APP_ID, AppConfig.SudGIP_APP_KEY, new SUDOPInitSDKListener() {
            @Override
            public void onSuccess() {
                if (canUpdateUI()) {
                    requestGameList(requestedSelectionId, pageNo, pageSize);
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                if (!canUpdateUI()) {
                    return;
                }
                showFailure(getString(R.string.game_list_api_init_failed, retCode, getErrorMessage(retMsg)));
            }
        });
    }

    private void requestGameList(Long selectionId, int pageNo, int pageSize) {
        if (selectionListMode) {
            requestSelectionList(pageNo, pageSize);
            return;
        }
        SUDOPGameListParams params = new SUDOPGameListParams();
        params.pageNo = pageNo;
        params.pageSize = pageSize;
        params.language = LanguageUtils.getLanguageTag(this);
        SUDOPGameListCallback callback = new SUDOPGameListCallback() {
            @Override
            public void onSuccess(SUDOPGameListResult result) {
                if (!canUpdateUI()) {
                    return;
                }
                records.clear();
                if (result.records != null) {
                    for (SUDOPGameListItem item : result.records) {
                        records.add(formatGameItem(item));
                    }
                }
                total = result.total;
                pageNoField.setText(String.valueOf(result.pageNo));
                String message = getString(R.string.game_list_api_request_success, result.pageNo, records.size(), result.total, result.pageSize);
                statusView.setText(message);
                Log.i(TAG, message);
                for (String item : records) {
                    Log.i(TAG, item);
                }
                adapter.notifyDataSetChanged();
                setRequestInProgress(false);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                if (!canUpdateUI()) {
                    return;
                }
                showFailure(getString(R.string.game_list_api_request_failed, retCode, getErrorMessage(retMsg)));
            }
        };
        if (selectionId == null) {
            SUDOP.getGameList(params, callback);
        } else {
            SUDOP.getGameList(selectionId, params, callback);
        }
    }

    private void showFailure(String message) {
        records.clear();
        selections.clear();
        total = 0;
        statusView.setText(message);
        Log.e(TAG, message);
        adapter.notifyDataSetChanged();
        setRequestInProgress(false);
    }

    private void setRequestInProgress(boolean inProgress) {
        requestInProgress = inProgress;
        requestButton.setEnabled(!inProgress);
        selectionIdField.setEnabled(!inProgress);
        pageNoField.setEnabled(!inProgress);
        pageSizeField.setEnabled(!inProgress);
        if (inProgress) {
            statusView.setText(R.string.game_list_api_requesting);
        }
        updatePageButtons();
    }

    private void updatePageButtons() {
        int pageNo = getPageNo();
        int pageSize = getPageSize();
        previousButton.setEnabled(!requestInProgress && pageNo > 0);
        long nextPageStart = ((long) pageNo + 1) * pageSize;
        nextButton.setEnabled(!requestInProgress && nextPageStart < total);
    }

    private int getPageNo() {
        return Math.max(0, parseInt(pageNoField.getText().toString(), 0));
    }

    private int getPageSize() {
        return Math.max(1, parseInt(pageSizeField.getText().toString(), 1));
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String getErrorMessage(String retMsg) {
        return TextUtils.isEmpty(retMsg) ? getString(R.string.game_list_api_unknown_error) : retMsg;
    }

    private boolean canUpdateUI() {
        return !isFinishing() && !isDestroyed();
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        currentFocus.clearFocus();
    }

    private String formatGameItem(SUDOPGameListItem item) {
        if (item == null) {
            return "";
        }
        return getString(R.string.game_list_api_item_format,
                emptyToString(item.gameId),
                String.valueOf(item.gameName == null ? Collections.emptyMap() : item.gameName),
                String.valueOf(item.icon == null ? Collections.emptyMap() : item.icon),
                emptyToString(item.version));
    }

    private String emptyToString(String value) {
        return value == null ? "" : value;
    }

    private void requestSelectionList(int pageNo, int pageSize) {
        SUDOPSelectionListParams params = new SUDOPSelectionListParams();
        params.pageNo = pageNo;
        params.pageSize = pageSize;
        params.language = LanguageUtils.getLanguageTag(this);
        SUDOP.getSelectionList(params, new SUDOPSelectionListCallback() {
            @Override
            public void onSuccess(SUDOPSelectionListResult result) {
                if (!canUpdateUI()) {
                    return;
                }
                selections.clear();
                records.clear();
                selections.addAll(result.records);
                for (SUDOPSelectionListItem item : selections) {
                    records.add(getString(R.string.sud_op_selection_item_format, item.selectionId, String.valueOf(item.name)));
                }
                total = result.total;
                pageNoField.setText(String.valueOf(result.pageNo));
                statusView.setText(getString(R.string.game_list_api_request_success,
                        result.pageNo, records.size(), result.total, result.pageSize));
                adapter.notifyDataSetChanged();
                setRequestInProgress(false);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                if (canUpdateUI()) {
                    showFailure(getString(R.string.game_list_api_request_failed, retCode, getErrorMessage(retMsg)));
                }
            }
        });
    }

    private class GameListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        GameListAdapter(@Nullable List<String> data) {
            super(R.layout.item_game_list_api_test, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String item) {
            holder.setText(R.id.tv_game_content, item);
        }
    }
}
