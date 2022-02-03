/* ************************************************************************************************
 * Copyright 2017 Qualcomm Technologies International, Ltd.                                       *
 **************************************************************************************************/

package com.zitech.audio.models.equalizer;

import com.zitech.audio.models.equalizer.parameters.Filter;
import com.zitech.audio.models.equalizer.parameters.Frequency;
import com.zitech.audio.models.equalizer.parameters.Gain;
import com.zitech.audio.models.equalizer.parameters.Parameter;
import com.zitech.audio.models.equalizer.parameters.ParameterType;
import com.zitech.audio.models.equalizer.parameters.Quality;

public class Band {
    /**
     * The band filter.
     */
    private Filter mFilter = Filter.BYPASS;
    /**
     * <p>To know if the filter is up to date and has the latest possible value from the device.</p>
     */
    private boolean isFilterUpToDate = false;

    /**
     * All the parameters which characterise on a band.
     */
    private final Parameter[] mParameters = new Parameter[ParameterType.getSize()];

    /**
     * <p>To build a new instance of the class Band. This will initialise all the parameters.</p>
     */
    Band() {
        mParameters[ParameterType.FREQUENCY.ordinal()] = new Frequency();
        mParameters[ParameterType.GAIN.ordinal()] = new Gain();
        mParameters[ParameterType.QUALITY.ordinal()] = new Quality();
    }

    /**
     * <p>To define the filter of the band.</p>
     * <p>This method will also update the configurability and the bounds of each parameter using the method
     * {@link Filter#defineParameters(Filter, Parameter, Parameter, Parameter) defineParameters}.</p>
     * <p>If the filter value is from the device, the band filter is considered as up to date.</p>
     *
     * @param filter
     *              The new filter.
     *
     * @param fromUser
     *              To know if this filter has been selected by the user or if it is an update from the device.
     */
    public void setFilter(Filter filter, boolean fromUser) {
        mFilter = filter;
        Filter.defineParameters(filter, mParameters[ParameterType.FREQUENCY.ordinal()],
                mParameters[ParameterType.GAIN.ordinal()], mParameters[ParameterType.QUALITY.ordinal()]);
        if (!fromUser) {
            isFilterUpToDate = true;
        }
    }

    /**
     * To get the filter of the band.
     *
     * @return the filter set up for this band.
     */
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * To get the frequency parameter of the band.
     *
     * @return the frequency parameter.
     */
    public Parameter getFrequency() {
        return mParameters[ParameterType.FREQUENCY.ordinal()];
    }

    /**
     * To get the gain parameter of the band.
     *
     * @return the gain parameter.
     */
    public Parameter getGain() {
        return mParameters[ParameterType.GAIN.ordinal()];
    }

    /**
     * To get the quality parameter of the band.
     *
     * @return the quality parameter.
     */
    public Parameter getQuality() {
        return mParameters[ParameterType.QUALITY.ordinal()];
    }

    /**
     * To know if all parameters of the band are considered as being up to date.
     *
     * @return true if all parameters are up to date, false if at least one of the parameters is out of date.
     */
    public boolean isUpToDate() {
        for (int i=1; i<mParameters.length; i++) {
            if (mParameters[i].isConfigurable() && !mParameters[i].isUpToDate()) {
                return false;
            }
        }
        return isFilterUpToDate;
    }

    /**
     * <p>To define the band as being out of date for each of its parameters.</p>
     */
    public void hasToBeUpdated() {
        isFilterUpToDate = false;
        for (int i=1; i<mParameters.length; i++) {
            mParameters[i].hasTobeUpdated();
        }
    }
}
