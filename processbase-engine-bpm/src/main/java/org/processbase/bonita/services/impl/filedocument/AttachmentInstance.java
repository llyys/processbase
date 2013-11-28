package org.processbase.bonita.services.impl.filedocument;

import org.ow2.bonita.facade.def.element.AttachmentDefinition;

/**
* Created by Lauri on 26.11.13.
*/
public class AttachmentInstance {

   private byte[] _data;
   private AttachmentDefinition attachmentDefinition;
   public void setData(byte[] _data) {
       this._data = _data;
   }

   public byte[] getData() {
       return _data;
   }

   public void setAttachmentDefinition(AttachmentDefinition attachmentDefinition) {
       this.attachmentDefinition = attachmentDefinition;
   }

   public AttachmentDefinition getAttachmentDefinition() {
       return attachmentDefinition;
   }

}
