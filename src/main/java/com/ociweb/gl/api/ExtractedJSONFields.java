package com.ociweb.gl.api;

import com.ociweb.json.JSONAccumRule;
import com.ociweb.pronghorn.struct.ByteSequenceValidator;
import com.ociweb.pronghorn.struct.DecimalValidator;
import com.ociweb.pronghorn.struct.LongValidator;

public interface ExtractedJSONFields<E extends ExtractedJSONFields> {
	
    public <T extends Enum<T>> E integerField(String extractionPath, T field);    
    public <T extends Enum<T>> E stringField(String extractionPath, T field);    
    public <T extends Enum<T>> E decimalField(String extractionPath, T field);    
    public <T extends Enum<T>> E booleanField(String extractionPath, T field);   
    
    public <T extends Enum<T>> E integerField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field);
    public <T extends Enum<T>> E stringField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field);
    public <T extends Enum<T>> E decimalField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field);
    public <T extends Enum<T>> E booleanField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field);
    
    public <T extends Enum<T>> E integerField(String extractionPath, T field, LongValidator validator);    
    public <T extends Enum<T>> E stringField(String extractionPath, T field, ByteSequenceValidator validator);    
    public <T extends Enum<T>> E decimalField(String extractionPath, T field, DecimalValidator validator);    

    public <T extends Enum<T>> E integerField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field, LongValidator validator);
    public <T extends Enum<T>> E stringField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field, ByteSequenceValidator validator);
    public <T extends Enum<T>> E decimalField(boolean isAligned, JSONAccumRule accumRule, String extractionPath, T field, DecimalValidator validator);
        
}
