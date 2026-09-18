package global.sud.gi.hello.app.ui.main;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.utils.GlobalSP;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class GameListAPITestActivityTest {

    private ActivityController<GameListAPITestActivity> controller;

    @Before
    public void setUp() {
        Utils.init(RuntimeEnvironment.getApplication());
        GlobalSP.getSP().clear();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.destroy();
        }
        GlobalSP.getSP().clear();
    }

    @Test
    public void legacyEntryKeepsOriginalTitleAndHidesSelectionId() {
        openPage(false);
        assertEquals(View.GONE, controller.get().findViewById(R.id.sud_op_selection_id).getVisibility());
        TextView title = controller.get().findViewById(R.id.sud_op_game_list_title);
        assertEquals(controller.get().getString(R.string.game_list_api_test_title), title.getText());
    }

    @Test
    public void selectionEntryShowsSelectionIdAndNewTitle() {
        openPage(true);
        assertEquals(View.VISIBLE, controller.get().findViewById(R.id.sud_op_selection_id).getVisibility());
        TextView title = controller.get().findViewById(R.id.sud_op_game_list_title);
        assertEquals(controller.get().getString(R.string.sud_op_selection_game_list), title.getText());
    }

    @Test
    public void invalidSelectionIdDoesNotStartRequest() {
        openPage(true);
        EditText selectionId = controller.get().findViewById(R.id.sud_op_selection_id);
        TextView status = controller.get().findViewById(R.id.tv_request_status);
        for (String invalid : new String[]{"", "-1", "9223372036854775808"}) {
            selectionId.setText(invalid);
            controller.get().findViewById(R.id.btn_request_game_list).performClick();
            assertNotNull(selectionId.getError());
            assertTrue(controller.get().findViewById(R.id.btn_request_game_list).isEnabled());
            assertEquals(controller.get().getString(R.string.sud_op_selection_game_list_hint), status.getText());
        }
    }

    private void openPage(boolean selectionMode) {
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), GameListAPITestActivity.class);
        intent.putExtra(GameListAPITestActivity.EXTRA_SELECTION_MODE, selectionMode);
        controller = Robolectric.buildActivity(GameListAPITestActivity.class, intent).create().start().resume();
    }

    @Test
    public void selectionListEntryDoesNotRequireSelectionId() {
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), GameListAPITestActivity.class);
        intent.putExtra(GameListAPITestActivity.EXTRA_SELECTION_LIST_MODE, true);
        controller = Robolectric.buildActivity(GameListAPITestActivity.class, intent).create().start().resume();
        assertEquals(View.GONE, controller.get().findViewById(R.id.sud_op_selection_id).getVisibility());
        TextView title = controller.get().findViewById(R.id.sud_op_game_list_title);
        assertEquals(controller.get().getString(R.string.sud_op_selection_list), title.getText());
        TextView status = controller.get().findViewById(R.id.tv_request_status);
        assertEquals(controller.get().getString(R.string.sud_op_selection_list_hint), status.getText());
    }

    @Test
    public void selectionGameEntryPrefillsExactIdWithoutStartingRequest() {
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), GameListAPITestActivity.class);
        intent.putExtra(GameListAPITestActivity.EXTRA_SELECTION_MODE, true);
        intent.putExtra(GameListAPITestActivity.EXTRA_SELECTION_ID, "9223372036854775807");
        controller = Robolectric.buildActivity(GameListAPITestActivity.class, intent).create().start().resume();
        EditText field = controller.get().findViewById(R.id.sud_op_selection_id);
        assertEquals("9223372036854775807", field.getText().toString());
        assertTrue(controller.get().findViewById(R.id.btn_request_game_list).isEnabled());
    }
}
