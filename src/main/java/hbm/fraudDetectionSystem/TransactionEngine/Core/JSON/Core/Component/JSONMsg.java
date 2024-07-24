package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hbm.fraudDetectionSystem.TransactionEngine.Exception.TransactionEngineException;

import java.util.StringTokenizer;

public class JSONMsg {
   protected ObjectNode fields = new ObjectMapper().createObjectNode();
   protected JSONBaseContainer container;

   public void pack(JsonNode prevNode) throws TransactionEngineException, JsonParseException {
      this.container.pack(this.fields, prevNode);
   }

   public void unpackHeader(JsonNode rawNode, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
      this.container.unpackHeader(this, rawNode, prevNode);
   }

   public void unpack(JsonNode rawNode, ObjectNode prevNode) throws JsonProcessingException, TransactionEngineException {
      this.container.unpack(this, rawNode, prevNode);
   }

   public void setContainer(JSONBaseContainer container) {
      this.container = container;
   }

   public void setField(String path, String value) {
      this.fields.put(path, value);
   }

   public void setField(String path, boolean value) {
      this.fields.put(path, value);
   }

   public void setField(String path, int value) {
      this.fields.put(path, value);
   }

   public void setField(String path, ArrayNode value) {
      this.fields.set(path, value);
   }

   public void setField(String path, ObjectNode value) {
      this.fields.set(path, value);
   }

   public void setField(String path, JsonNode value) {
      this.fields.set(path, value);
   }

   public ObjectNode getFields() {
      return fields;
   }

   public String getValue(String path) {
      if (path == null) {
         return null;
      }
      StringTokenizer st = new StringTokenizer(path, ".");
      JsonNode co = this.fields;

      while (true) {
         co = co.findPath(st.nextToken());

         if (!st.hasMoreTokens()) {
            return co.asText();
         }
      }
   }
}
