/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

public class LocaleChangeListener implements ValueChangeListener
{

    @Override
    public void processValueChange(ValueChangeEvent event)
            throws AbortProcessingException
    {

        //access country bean directly
        HotelBookClientBean bean = (HotelBookClientBean) FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap().get("hotelBookBean");
        bean.setCityName(event.getNewValue().toString());
    }
}
