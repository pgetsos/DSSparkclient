/*
 * Copyright (c) 2018.
 * Created for MSc in Computer Science - Distributed Systems
 * All right reserved except otherwise noted
 */

package auebdreamteam.com.dssparkclient.ui;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import auebdreamteam.com.dssparkclient.R;
import auebdreamteam.com.dssparkclient.databinding.FragmentBusDelayPerStopBinding;


public class DelayPerStopFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FragmentBusDelayPerStopBinding binding;
	private DatePickerDialog dpd;
	private Date day = new Date();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_bus_delay_per_stop, container, false);

        binding.dateButton.setOnClickListener(view -> dpd.show(getFragmentManager(), "DatePicker"));
//        binding.searchButton.setOnClickListener(view -> sendSearchRequest());

        initializeDatePicker();

		return binding.getRoot();
    }

    private void initializeDatePicker() {
		Calendar startOfData = Calendar.getInstance();
		startOfData.set(2013, 0, 1);
		day = startOfData.getTime();
		String date = getFormattedDate(startOfData);
		binding.dateButton.setText(date);

		Calendar endOfData = Calendar.getInstance();
		endOfData.set(2013, 0, 31);

		dpd =  DatePickerDialog.newInstance(this, startOfData);
		dpd.setVersion(DatePickerDialog.Version.VERSION_1);
		dpd.showYearPickerFirst(false);
		dpd.setMinDate(startOfData);
		dpd.setMaxDate(endOfData);
		dpd.setCancelColor(getResources().getColor(R.color.black));
		dpd.setOkColor(getResources().getColor(R.color.black));
	}

    private void sendSearchRequest() {
        if (binding.busLineInput.length() == 0) {
            binding.busLineInput.setError(getString(R.string.input_is_required));
            binding.busLineInput.requestFocus();
            return;
        }
        if (binding.busStopInput.length() == 0) {
            binding.busStopInput.setError(getString(R.string.input_is_required));
            binding.busStopInput.requestFocus();
            return;
        }

        String busLine = binding.busLineInput.getText().toString();
        String busStop = binding.busStopInput.getText().toString();
    }

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		Calendar picked = new GregorianCalendar();
		picked.set(year, monthOfYear, dayOfMonth);
		day = picked.getTime();
		String date = getFormattedDate(picked);

		binding.dateButton.setText(date);

	}

	private String getFormattedDate(Calendar calendar) {
		if(calendar.getTimeInMillis() == 0) {
			return "-";
		}
		return calendar.get(Calendar.DAY_OF_MONTH)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
	}

}
