import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.TKBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/15/17.
 */

public class ThoiKhoaBieu extends Fragment {
    View rootView;
    ListView lvTKB;
    private TKBAdapter tkbAdapter;
    private ArrayList<LopHP> lstLopHP;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tkb, container, false);

        getWidgets();
        setWidgets();
        setWidgetsEvent();

        return rootView;
    }

    private void init() {
        lstLopHP = new ArrayList<>(DBLopHPHelper.getsInstance().getListUserLopHP());
        tkbAdapter = new TKBAdapter(getActivity(), android.R.layout.simple_list_item_1, lstLopHP);
    }

    private void getWidgets() {
        lvTKB = (ListView) rootView.findViewById(R.id.lvTKB);
    }

    private void setWidgets() {
        lvTKB.setAdapter(tkbAdapter);
    }

    private void setWidgetsEvent() {

    }
}
