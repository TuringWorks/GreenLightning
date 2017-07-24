package com.ociweb.gl.impl;

import java.lang.reflect.Field;

import com.ociweb.gl.api.MsgRuntime;
import com.ociweb.pronghorn.pipe.util.hash.IntHashTable;

/**
 * Scans up the object tree to find instances of specific classes
 * @author Nathan Tippy
 *
 */
public class ChildClassScanner {

	public static boolean notPreviouslyHeld(Object child, 
			                                   Object topParent, 
			                                   IntHashTable usageChecker) {
	    int hash = System.identityHashCode(child);
	    int parentHash = System.identityHashCode(topParent);           
	    if (IntHashTable.hasItem(usageChecker, hash)) {
	    	if (parentHash!=IntHashTable.getItem(usageChecker, hash)) {
	    		return false;
	    	}
	    } 
	    //keep so this is detected later if use
	    IntHashTable.setItem(usageChecker, hash, parentHash);
	    return true;
	}
	///////////
	///////////

	static boolean visitByClass(Object listener, int depth, 
												 ChildClassScannerVisitor visitor,
												 Class<? extends Object> c,
												 Class targetType, 
												 Object topParent) {
			
			Field[] fields = c.getDeclaredFields();
	                        
	        int f = fields.length;
	        while (--f >= 0) {
	      
	                fields[f].setAccessible(true);   
	                Object obj = null;
					try {
						obj = fields[f].get(listener);
						if (targetType.isInstance(obj)) {
							if (!visitor.visit(obj, topParent)) {
								return false;
							}                              
						} else {      
							
							if (    (obj!=null)
									&& (obj.getClass()!=null)
									&&  (!obj.getClass().isPrimitive()) 
									&& (obj != listener) 
									&& (!obj.getClass().getName().startsWith("java."))  
									&& (!obj.getClass().isEnum())
									&& (!(obj instanceof MsgRuntime))               		
									&& !fields[f].isSynthetic()
									&& fields[f].isAccessible() 
									&& depth<=7) { //stop recursive depth
								
//								                		if (depth >4) {
//								                			System.out.println(depth+" "+obj.getClass().getName());
//								                		}
								//recursive check for command channels
								if (!visitUsedByClass(obj, depth+1, visitor, topParent, targetType)) {
									return false;
								}
							}
						}
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}                                
	   
	        }
	        return true;
		}

	static boolean visitUsedByClass(Object obj, int depth, 
			     ChildClassScannerVisitor visitor, 
			     Object topParent, Class targetType) {
	    if (null!=obj) {
		    Class<? extends Object> c = obj.getClass();
		    while (null != c) {
		    	if (!visitByClass(obj, depth, visitor, c, targetType, topParent)) {
		    		return false;
		    	}
		    	c = c.getSuperclass();
		    }
	    }
	    return true;
	}

	public static boolean visitUsedByClass(Object listener, ChildClassScannerVisitor visitor, Class target) {
	
		return visitUsedByClass(listener, 0, visitor, listener, target);
	}

}