package com.BatchUdemyExample.BatchDemoUdm.reader;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.util.Arrays;
import java.util.List;


public class InMemReader extends AbstractItemStreamItemReader {
    Integer[] Intarray = {1,2,3,4,5,6,7,8};
    List<Integer> myList  = Arrays.asList(Intarray);

    int index = 0;

    @Override
    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Integer nextItem = null;
        if(index < myList.size()){
            nextItem = myList.get(index);
            index++;
        }else{
            index=0;
        }
        return nextItem;
        }
    }

