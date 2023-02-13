package com.example.startendo;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;
import com.example.startendo.ColorPickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

public class DrawingActivity extends AppCompatActivity implements ColorPickerDialog.OnColorChangedListener {
    /* access modifiers changed from: private */
    public String boardId = "";
    /* access modifiers changed from: private */
    public int mBoardHeight;
    /* access modifiers changed from: private */
    public int mBoardWidth;
    /* access modifiers changed from: private */
    public DrawingView mDrawingView;
    private DatabaseReference mSegmentsRef;

    /* renamed from: no */
    int no;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.boardId = getIntent().getStringExtra("BOARD_ID");
        DatabaseReference child = FirebaseDatabase.getInstance().getReference().child("boardmetas").child(this.boardId);
        DatabaseReference child2 = FirebaseDatabase.getInstance().getReference().child("boardsegments").child(this.boardId);
        this.mSegmentsRef = child2;
        child2.addChildEventListener(new ChildEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DrawingView unused = DrawingActivity.this.mDrawingView = new DrawingView(DrawingActivity.this, FirebaseDatabase.getInstance().getReference().child("boardsegments").child(DrawingActivity.this.boardId), DrawingActivity.this.mBoardWidth, DrawingActivity.this.mBoardHeight);
                DrawingActivity drawingActivity = DrawingActivity.this;
                drawingActivity.setContentView((View) drawingActivity.mDrawingView);
            }
        });
        child.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (DrawingActivity.this.mDrawingView != null) {
                    ((ViewGroup) DrawingActivity.this.mDrawingView.getParent()).removeView(DrawingActivity.this.mDrawingView);
                    DrawingActivity.this.mDrawingView.cleanup();
                    DrawingView unused = DrawingActivity.this.mDrawingView = null;
                }
                Map map = (Map) dataSnapshot.getValue();
                if (map != null && map.get("width") != null && map.get("height") != null) {
                    int unused2 = DrawingActivity.this.mBoardWidth = ((Long) map.get("width")).intValue();
                    int unused3 = DrawingActivity.this.mBoardHeight = ((Long) map.get("height")).intValue();
                    DrawingView unused4 = DrawingActivity.this.mDrawingView = new DrawingView(DrawingActivity.this, FirebaseDatabase.getInstance().getReference().child("boardsegments").child(DrawingActivity.this.boardId), DrawingActivity.this.mBoardWidth, DrawingActivity.this.mBoardHeight);
                    DrawingActivity drawingActivity = DrawingActivity.this;
                    drawingActivity.setContentView((View) drawingActivity.mDrawingView);
                }
            }
        });
        getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paint_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.clear) {
            this.mSegmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onCancelled(DatabaseError databaseError) {
                }

                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        if (next.child("user").getValue().equals(FirebaseAuth.getInstance().getUid())) {
                            next.getRef().removeValue();
                        }
                    }
                }
            });
            return true;
        } else if (itemId == R.id.eraser) {
            this.mSegmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onCancelled(DatabaseError databaseError) {
                }

                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i = 1;
                      no = 1;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        child.child("user").getValue().equals(FirebaseAuth.getInstance().getUid());
                       no++;
                    }
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        i=i+1;
                        if (next.child("user").getValue().equals(FirebaseAuth.getInstance().getUid()) && DrawingActivity.this.no == i){
                            next.getRef().removeValue();
                        }

                    }

                }
            });
            return true;
        } else if (itemId != R.id.pen) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            new ColorPickerDialog(this, this, 0xFFFFFFFF).show();
            return true;
        }
    }

    public void colorChanged(int i) {
        this.mDrawingView.setColor(i);
    }
}
