package com.example.startendo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrawingView extends View {
    public static final int PIXEL_SIZE = 8;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Canvas mBuffer;
    private int mCanvasHeight;
    private int mCanvasWidth;
    private int mCurrentColor;
    private Segment mCurrentSegment;
    private DatabaseReference mFirebaseRef;
    private int mLastX;
    private int mLastY;
    private ChildEventListener mListener;
    public Set<String> mOutstandingSegments;
    private Paint mPaint;
    private Path mPath;
    private float mScale;

    public DrawingView(Context context, DatabaseReference databaseReference) {
        this(context, databaseReference, 1.0f);
    }

    public DrawingView(Context context, DatabaseReference databaseReference, int i, int i2) {
        this(context, databaseReference);
        setBackgroundColor(-12303292);
        this.mCanvasWidth = i;
        this.mCanvasHeight = i2;
    }

    public DrawingView(Context context, DatabaseReference databaseReference, float f) {
        super(context);
        this.mCurrentColor = -1;
        this.mScale = 1.0f;
        this.mOutstandingSegments = new HashSet();
        this.mPath = new Path();
        this.mFirebaseRef = databaseReference;
        this.mScale = f;
        this.mListener = databaseReference.addChildEventListener(new ChildEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                String key = dataSnapshot.getKey();
                dataSnapshot.child("user").getValue().toString();
                if (!DrawingView.this.mOutstandingSegments.contains(key)) {
                    Segment segment = (Segment) dataSnapshot.getValue(Segment.class);
                    DrawingView.this.drawSegment(segment, DrawingView.paintFromColor(segment.getColor()));
                    DrawingView.this.invalidate();
                }
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DrawingView.this.invalidate();
            }
        });
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mBitmapPaint = new Paint(4);
    }

    public void cleanup() {
        this.mFirebaseRef.removeEventListener(this.mListener);
    }

    public void setColor(int i) {
        this.mCurrentColor = i;
        this.mPaint.setColor(i);
    }

    public void clear() {
        this.mBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        this.mBuffer = new Canvas(this.mBitmap);
        invalidate();
    }

    public void test() {
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        getResources();
        float min = Math.min((((float) i) * 1.0f) / ((float) mCanvasWidth), (((float) i2) * 1.0f) / ((float) mCanvasHeight));
        mScale = min;
        mBitmap = Bitmap.createBitmap(Math.round(((float) mCanvasWidth) * min), Math.round(((float)mCanvasHeight) * this.mScale), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable drawable = getResources().getDrawable(R.drawable.miragelayout);
        drawable.setBounds(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
        drawable.draw(canvas);
        canvas.drawColor(0);
        canvas.drawRect(0.0f, 0.0f, (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight(), paintFromColor(0, Paint.Style.FILL_AND_STROKE));
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    public static Paint paintFromColor(int i) {
        return paintFromColor(i, Paint.Style.STROKE);
    }

    public static Paint paintFromColor(int i, Paint.Style style) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(i);
        paint.setStyle(style);
        return paint;
    }


    public static Path getPathForPoints(List<Point> points, double scale) {
        Path path = new Path();
        scale = scale * PIXEL_SIZE;
        Point current = points.get(0);
        path.moveTo(Math.round(scale * current.x), Math.round(scale * current.y));
        Point next = null;
        for (int i = 1; i < points.size(); ++i) {
            next = points.get(i);
            path.quadTo(
                    Math.round(scale * current.x), Math.round(scale * current.y),
                    Math.round(scale * (next.x + current.x) / 2), Math.round(scale * (next.y + current.y) / 2)
            );
            current = next;
        }
        if (next != null) {
            path.lineTo(Math.round(scale * next.x), Math.round(scale * next.y));
        }
        return path;
    }

    /* access modifiers changed from: private */

    private void drawSegment(Segment segment, Paint paint) {
        if (mBuffer != null) {
            mBuffer.drawPath(getPathForPoints(segment.getPoints(), mScale), paint);
        }
    }

    private void onTouchStart(float f, float f2) {
        this.mPath.reset();
        this.mPath.moveTo(f, f2);
        Segment segment = new Segment(this.mCurrentColor, FirebaseAuth.getInstance().getUid());
        this.mCurrentSegment = segment;
        int i = ((int) f) / 8;
        this.mLastX = i;
        int i2 = ((int) f2) / 8;
        this.mLastY = i2;
        segment.addPoint(i, i2);
    }

    private void onTouchMove(float f, float f2) {
        int i = ((int) f) / 8;
        int i2 = ((int) f2) / 8;
        float abs = (float) Math.abs(i2 - this.mLastY);
        if (((float) Math.abs(i - this.mLastX)) >= 1.0f || abs >= 1.0f) {
            Path path = this.mPath;
            int i3 = this.mLastX;
            int i4 = this.mLastY;
            path.quadTo((float) (i3 * 8), (float) (i4 * 8), (float) (((i3 + i) * 8) / 2), (float) (((i4 + i2) * 8) / 2));
            this.mLastX = i;
            this.mLastY = i2;
            this.mCurrentSegment.addPoint(i, i2);
        }
    }

    private void onTouchEnd() {
        this.mPath.lineTo((float) (this.mLastX * 8), (float) (this.mLastY * 8));
        this.mBuffer.drawPath(this.mPath, this.mPaint);
        this.mPath.reset();
        DatabaseReference push = this.mFirebaseRef.push();
        final String key = push.getKey();
        this.mOutstandingSegments.add(key);
        Segment segment = new Segment(this.mCurrentSegment.getColor(), this.mCurrentSegment.getUser());
        for (Point next : this.mCurrentSegment.getPoints()) {
            segment.addPoint(Math.round(((float) next.x) / this.mScale), Math.round(((float) next.y) / this.mScale));
        }
        push.setValue((Object) segment, (DatabaseReference.CompletionListener) new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    DrawingView.this.mOutstandingSegments.remove(key);
                    return;
                }
                throw databaseError.toException();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            onTouchStart(x, y);
            invalidate();
        } else if (action == 1) {
            onTouchEnd();
            invalidate();
        } else if (action == 2) {
            onTouchMove(x, y);
            invalidate();
        }
        return true;
    }
}
