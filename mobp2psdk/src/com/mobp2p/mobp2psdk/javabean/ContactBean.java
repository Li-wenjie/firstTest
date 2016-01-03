package com.mobp2p.mobp2psdk.javabean;

import com.mobp2p.mobp2psdk.utils.MetaDataUtils;

public class ContactBean {
  public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getContactName() {
		return contactName;
	}

public String contactName;
  public String contactTel;

    public void setTelNumber(String data1) {
        contactTel = MetaDataUtils.formatPhontNumber(data1);
    }

    public void setContactName(String data1) {
        contactName = MetaDataUtils.StringFilter(data1);
    }
}
