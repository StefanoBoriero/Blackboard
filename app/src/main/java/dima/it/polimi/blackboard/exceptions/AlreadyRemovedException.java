package dima.it.polimi.blackboard.exceptions;

import com.google.firebase.firestore.DocumentSnapshot;

import dima.it.polimi.blackboard.model.TodoItem;

/**
 * Exception raised if two people concurrently modify the same item
 * Created by Stefano on 17/03/2018.
 */

public class AlreadyRemovedException extends Exception {
    private DocumentSnapshot removedDocument;
    private TodoItem removedItem;

    public AlreadyRemovedException(DocumentSnapshot doc){
        removedDocument = doc;
    }

    public AlreadyRemovedException(TodoItem item){
        removedItem = item;
    }

    @Override
    public String getMessage() {
        String id;
        if(removedItem != null){
            id = removedItem.getId();
        }
        else{
            id = removedDocument.getId();
        }
        return "Error in deleting document " + id + ": not found in the adapter";
    }
}
