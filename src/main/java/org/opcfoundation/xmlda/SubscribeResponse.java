/******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 * 
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 * 
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

/**
 * SubscribeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:23:23 CEST)
 */

            
                package org.opcfoundation.xmlda;
            

            /**
            *  SubscribeResponse bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class SubscribeResponse
        implements org.apache.axis2.databinding.ADBBean{
        
                public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://opcfoundation.org/webservices/XMLDA/1.0/",
                "SubscribeResponse",
                "ns1");

            

                        /**
                        * field for SubscribeResult
                        */

                        
                                    protected org.opcfoundation.xmlda.ReplyBase localSubscribeResult ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSubscribeResultTracker = false ;

                           public boolean isSubscribeResultSpecified(){
                               return localSubscribeResultTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return org.opcfoundation.webservices.xmlda._1_0.ReplyBase
                           */
                           public  org.opcfoundation.xmlda.ReplyBase getSubscribeResult(){
                               return localSubscribeResult;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SubscribeResult
                               */
                               public void setSubscribeResult(org.opcfoundation.xmlda.ReplyBase param){
                            localSubscribeResultTracker = param != null;
                                   
                                            this.localSubscribeResult=param;
                                    

                               }
                            

                        /**
                        * field for RItemList
                        */

                        
                                    protected org.opcfoundation.xmlda.SubscribeReplyItemList localRItemList ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localRItemListTracker = false ;

                           public boolean isRItemListSpecified(){
                               return localRItemListTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return org.opcfoundation.webservices.xmlda._1_0.SubscribeReplyItemList
                           */
                           public  org.opcfoundation.xmlda.SubscribeReplyItemList getRItemList(){
                               return localRItemList;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param RItemList
                               */
                               public void setRItemList(org.opcfoundation.xmlda.SubscribeReplyItemList param){
                            localRItemListTracker = param != null;
                                   
                                            this.localRItemList=param;
                                    

                               }
                            

                        /**
                        * field for Errors
                        * This was an Array!
                        */

                        
                                    protected org.opcfoundation.xmlda.OPCError[] localErrors ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localErrorsTracker = false ;

                           public boolean isErrorsSpecified(){
                               return localErrorsTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return org.opcfoundation.webservices.xmlda._1_0.OPCError[]
                           */
                           public  org.opcfoundation.xmlda.OPCError[] getErrors(){
                               return localErrors;
                           }

                           
                        


                               
                              /**
                               * validate the array for Errors
                               */
                              protected void validateErrors(org.opcfoundation.xmlda.OPCError[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Errors
                              */
                              public void setErrors(org.opcfoundation.xmlda.OPCError[] param){
                              
                                   validateErrors(param);

                               localErrorsTracker = param != null;
                                      
                                      this.localErrors=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param org.opcfoundation.webservices.xmlda._1_0.OPCError
                             */
                             public void addErrors(org.opcfoundation.xmlda.OPCError param){
                                   if (localErrors == null){
                                   localErrors = new org.opcfoundation.xmlda.OPCError[]{};
                                   }

                            
                                 //update the setting tracker
                                localErrorsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localErrors);
                               list.add(param);
                               this.localErrors =
                             (org.opcfoundation.xmlda.OPCError[])list.toArray(
                            new org.opcfoundation.xmlda.OPCError[list.size()]);

                             }
                             

                        /**
                        * field for ServerSubHandle
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localServerSubHandle ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getServerSubHandle(){
                               return localServerSubHandle;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ServerSubHandle
                               */
                               public void setServerSubHandle(java.lang.String param){
                            
                                            this.localServerSubHandle=param;
                                    

                               }
                            

     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
               org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME);
               return factory.createOMElement(dataSource,MY_QNAME);
            
        }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               javax.xml.stream.XMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();
                    writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://opcfoundation.org/webservices/XMLDA/1.0/");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":SubscribeResponse",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "SubscribeResponse",
                           xmlWriter);
                   }

               
                   }
               
                                            if (localServerSubHandle != null){
                                        
                                                writeAttribute("",
                                                         "ServerSubHandle",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServerSubHandle), xmlWriter);

                                            
                                      }
                                     if (localSubscribeResultTracker){
                                            if (localSubscribeResult==null){
                                                 throw new org.apache.axis2.databinding.ADBException("SubscribeResult cannot be null!!");
                                            }
                                           localSubscribeResult.serialize(new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","SubscribeResult"),
                                               xmlWriter);
                                        } if (localRItemListTracker){
                                            if (localRItemList==null){
                                                 throw new org.apache.axis2.databinding.ADBException("RItemList cannot be null!!");
                                            }
                                           localRItemList.serialize(new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","RItemList"),
                                               xmlWriter);
                                        } if (localErrorsTracker){
                                       if (localErrors!=null){
                                            for (int i = 0;i < localErrors.length;i++){
                                                if (localErrors[i] != null){
                                                 localErrors[i].serialize(new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","Errors"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("Errors cannot be null!!");
                                        
                                    }
                                 }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://opcfoundation.org/webservices/XMLDA/1.0/")){
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }
        
        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName,attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace,attName,attValue);
            }
        }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    java.lang.String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localSubscribeResultTracker){
                            elementList.add(new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/",
                                                                      "SubscribeResult"));
                            
                            
                                    if (localSubscribeResult==null){
                                         throw new org.apache.axis2.databinding.ADBException("SubscribeResult cannot be null!!");
                                    }
                                    elementList.add(localSubscribeResult);
                                } if (localRItemListTracker){
                            elementList.add(new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/",
                                                                      "RItemList"));
                            
                            
                                    if (localRItemList==null){
                                         throw new org.apache.axis2.databinding.ADBException("RItemList cannot be null!!");
                                    }
                                    elementList.add(localRItemList);
                                } if (localErrorsTracker){
                             if (localErrors!=null) {
                                 for (int i = 0;i < localErrors.length;i++){

                                    if (localErrors[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/",
                                                                          "Errors"));
                                         elementList.add(localErrors[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("Errors cannot be null!!");
                                    
                             }

                        }
                            attribList.add(
                            new javax.xml.namespace.QName("","ServerSubHandle"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServerSubHandle));
                                

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static SubscribeResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            SubscribeResponse object =
                new SubscribeResponse();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"SubscribeResponse".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SubscribeResponse)org.opcfoundation.xmlda.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    // handle attribute "ServerSubHandle"
                    java.lang.String tempAttribServerSubHandle =
                        
                                reader.getAttributeValue(null,"ServerSubHandle");
                            
                   if (tempAttribServerSubHandle!=null){
                         java.lang.String content = tempAttribServerSubHandle;
                        
                                                 object.setServerSubHandle(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribServerSubHandle));
                                            
                    } else {
                       
                    }
                    handledAttributes.add("ServerSubHandle");
                    
                    
                    reader.next();
                
                        java.util.ArrayList list3 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","SubscribeResult").equals(reader.getName())){
                                
                                                object.setSubscribeResult(org.opcfoundation.xmlda.ReplyBase.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","RItemList").equals(reader.getName())){
                                
                                                object.setRItemList(org.opcfoundation.xmlda.SubscribeReplyItemList.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","Errors").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list3.add(org.opcfoundation.xmlda.OPCError.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone3 = false;
                                                        while(!loopDone3){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone3 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("http://opcfoundation.org/webservices/XMLDA/1.0/","Errors").equals(reader.getName())){
                                                                    list3.add(org.opcfoundation.xmlda.OPCError.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone3 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setErrors((org.opcfoundation.xmlda.OPCError[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                org.opcfoundation.xmlda.OPCError.class,
                                                                list3));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
    
