/*
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package cz.honzovysachy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import cz.honzovysachy.mysleni.Minimax;
import cz.honzovysachy.pravidla.PawnPromotionGUIQueen;
import cz.honzovysachy.pravidla.Pozice;
import cz.honzovysachy.pravidla.SavedTask;
import cz.honzovysachy.pravidla.Task;
import java.util.Vector;



public class SachoveView extends View
{
	int mcx = 4;
	int mcy = 4;
	int mox = -1;
	int moy = -1;
    int mPole = -1;
	boolean mOtoceno = false;
	boolean mBilyClovek = true;
	boolean mCernyClovek = false;
	boolean mPremyslim = false;
	CharSequence mFigury[];
	Task mTask;
	final Handler mHandler = new Handler();
//	byte[] mSchPriMysleni = new byte[Pozice.h8 + 1];
	int mFieldFrom;
	int mFieldTo;

	protected boolean hrajeClovek()
	{
		return !isPremyslim() && (mBilyClovek && mTask.mBoardComputing.bily ||
			mCernyClovek && !mTask.mBoardComputing.bily);
	}

	public void saveInstanceState(Bundle bundle)
	{
		bundle.putSerializable("TASK", mTask.getSavedTask());
	}

    public SachoveView(Activity a, Bundle bundle)
	{
        super(a);
       	mTask = new Task(bundle == null ? null : (SavedTask)bundle.get("TASK"));
        setFocusable(true);
        mFigury = new CharSequence[6];
        mFigury[0] = "p";
        mFigury[1] = "r";
        mFigury[2] = "s";
        mFigury[3] = "b";
        mFigury[4] = "q";
        mFigury[5] = "k";

        pripravTahHned();
    }

    public void pripravTah()
	{
    	mHandler.post(new Runnable() {
				public void run()
				{
					pripravTahHned();
				}
			});
    }

    public void tahni(int tah)
	{
    	mox = -1;
    	moy = -1;
    	mTask.tahni(tah, true, true, null);
    	if (mTask.mEnd != 0) dlg(mTask.getEndOfGameString(mTask.mEnd));
    	invalidate();
    	pripravTah();
    }

    protected void pripravTahHned()
	{
    	if (hrajeClovek())
		{
    	}
		else
		{
			tahniPrograme();
    	}

    }

    public void otoc()
	{
    	mOtoceno = !mOtoceno;
    	invalidate();
    }

    public void replayPromotion(int type)
	{
    	Vector t = mTask.nalezTahyVector();
    	int tah = mTask.makeMove(t, mFieldFrom, mFieldTo, new PawnPromotionGUIQueen(type + 2));
		tahni(tah);
    }

    public boolean onTouchEvent(MotionEvent event)
	{
    	if (isPremyslim()) return false;
     	if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
    	if (mPole <= 0 || !hrajeClovek()) return false;
    	int x = (int)((event.getX() + 0.5) / mPole);
    	int y = (int)((event.getY() + 0.5) / mPole);
    	if (x > 7 || x < 0 || y > 7 || y < 0) return false;
    	if (mOtoceno)
		{
    		x = 7 - x;
    	}
		else
		{
    		y = 7 - y;
    	}

    	Vector t = mTask.nalezTahyVector();
		int pole = Pozice.a1 + x + 10 * y;
		if (mTask.JeTam1(t, pole))
		{
			mcx = mox = x;
			mcy = moy = y;
			invalidate();
			return true;
		}
		int pole1 = Pozice.a1 + mox + 10 * moy;
		if (mTask.JeTam2(t, pole1, pole))
		{
			if (Math.abs(mTask.mBoardComputing.sch[pole1]) == 1 && (y == 7 || y == 0))
			{
				mFieldFrom = pole1;
				mFieldTo = pole;
				Intent result = new Intent();
		        result.setClass(getContext(), PromotionActivity.class);
				((Activity)(getContext())).startActivityForResult(result, 0);
				return true;
			}
			int tah = mTask.makeMove(t, pole1, pole, new PawnPromotionGUIQueen());
			tahni(tah);
			return true;
		}
		mcx = x;
		mcy = y;

		return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
	{
    	byte[] sch = null;
		/*	if (isPremyslim()) {
		 sch = mSchPriMysleni;
		 } else {*/
		sch = mTask.mBoard.sch;
		//	}
    	Rect r = new Rect();
    	getDrawingRect(r);
  		int w = r.right - r.left;//canvas.getWidth();
   		int h = r.bottom - r.top;//canvas.getHeight() - 50;
        mPole = (w < h ? w : h);
        mPole >>= 3; //mPole /= 8;
		
        Paint bila = new Paint();
        bila.setARGB(255, 200, 200, 200);
		
		
        Paint cerna = new Paint();
        cerna.setARGB(255, 100, 100, 100);
		
		
        Paint modra = new Paint();
        modra.setARGB(255, 0, 0, 255);
		
		
        Paint zelena = new Paint();
        zelena.setARGB(255, 0, 255, 0);
		
		
        Paint bilaf = new Paint();
        bilaf.setARGB(255, 255, 255, 255);
		bilaf.setAntiAlias(true);
		bilaf.setLinearText(true);
		bilaf.setTextSize(30);
		
        Paint cernaf = new Paint();
        cernaf.setARGB(255, 0, 0, 0);
		cernaf.setAntiAlias(true);
		cernaf.setLinearText(true);
		cernaf.setTextSize(30);
		
        boolean clovek = hrajeClovek();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
			{
            	Paint p;
            	if (clovek && mox == i && moy == j)
				{
            		p = zelena;
            	}
				else
            	if (clovek && mcx == i && mcy == j)
				{
            		p = modra;
            	}
				else
				{
            		p = ((((i + j) & 1) == 1) ? bila : cerna);
            	}
            	int sx = (mOtoceno ? 7 - i : i) * mPole;
            	int sy = (mOtoceno ? j : 7 - j) * mPole;
                canvas.drawRect(new Rect(sx, sy, sx + mPole, sy + mPole), p);
                byte co = sch[Pozice.a1 + i + 10 * j];

                if (co != 0 && co > -7 && co < 7)
				{
					if (co > 0)
					{
						canvas.drawText(mFigury[co - 1].toString().toUpperCase(), sx + 8 , sy -8 + mPole, bilaf);
					}
					else
					{
						canvas.drawText(mFigury[-co - 1].toString().toUpperCase(), sx + 8, sy -8 + mPole, cernaf);
					}
				}

			}
		//CharSequence dr = mFigury[co > 0 ? 1 : 0][co > 0 ? co -1 : -co - 1];
		//dr.setBounds(sx, sy, sx + mPole, sy + mPole);


	}



	public void dlg(String co)
	{
		Dialog d = new Dialog(this.getContext());
		d.setTitle(co);
		d.show();
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
    	if (isPremyslim()) return false;
    	switch (keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_UP:
				if (!mOtoceno && mcy < 7 || mOtoceno && mcy > 0)
				{
					if (mOtoceno) mcy--; else mcy++;
					invalidate();
				}
				return true;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (mOtoceno && mcy < 7 || !mOtoceno && mcy > 0)
				{
					if (mOtoceno) mcy++; else mcy--;
					invalidate();
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (!mOtoceno && mcx < 7 || mOtoceno && mcx > 0)
				{
					if (mOtoceno) mcx--; else mcx++;
					invalidate();
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (mOtoceno && mcx < 7 || !mOtoceno && mcx > 0)
				{
					if (mOtoceno) mcx++; else mcx--;
					invalidate();
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if (!hrajeClovek()) return true;
				Vector t = mTask.nalezTahyVector();
				int pole = Pozice.a1 + mcx + 10 * mcy;
				if (mTask.JeTam1(t, pole))
				{
					mox = mcx;
					moy = mcy;
					invalidate();
					return true;
				}
				int pole1 = Pozice.a1 + mox + 10 * moy;
				if (mTask.JeTam2(t, pole1, pole))
				{
					if (Math.abs(mTask.mBoardComputing.sch[pole1]) == 1 && (mcy == 7 || mcy == 0))
					{
						mFieldFrom = pole1;
						mFieldTo = pole;
						Intent result = new Intent();
						result.setClass(getContext(), PromotionActivity.class);
						((Activity)(getContext())).startActivityForResult(result, 0);
						return true;
					}
					else
					{
						int tah = mTask.makeMove(t, pole1, pole, new PawnPromotionGUIQueen());
						tahni(tah);
						return true;
					}
				}

				return true;
    	}
    	return false;
    }

    public boolean isPremyslim()
	{
    	return mPremyslim;
    }




    protected void tahniPrograme()
	{
    	mPremyslim = true;
		//   	System.arraycopy(mTask.mBoardComputing.sch, 0, mSchPriMysleni, 0, Pozice.h8 + 1);
    	Thread t = new Thread() {
    		public void run()
			{
    			mTask.nalezTahyVector();
    			final int tah;
    			tah = Minimax.minimax(mTask, 5000, null); 
    			mHandler.post(
    				new Runnable() {
						public void run()
						{
							mPremyslim = false;
							if (tah != 0) 
								tahni(tah);
						}}
				);
			}
    	};
    	t.start();
	}

    protected void restart()
	{
    	mTask = new Task(null);
    	invalidate();
    }

    protected void undo()
	{
    	if (mTask.mIndexInGame >= 0)
		{
    		mTask.tahniZpet(0, true, null);
    		mBilyClovek = mTask.mBoardComputing.bily;
			mCernyClovek = !mTask.mBoardComputing.bily;
    		invalidate();
    	}
    }

    protected void redo()
	{
    	if (mTask.mIndexInGame + 1 < mTask.mGame.size())
		{
			mTask.tahni(0, true, false, null);
			mBilyClovek = mTask.mBoardComputing.bily;
			mCernyClovek = !mTask.mBoardComputing.bily;
			invalidate();
		}
    }

    protected void move()
	{
    	if (mTask.mBoardComputing.bily)
		{
    		mBilyClovek = false;
    		mCernyClovek = true;
    	}
		else
		{
    		mBilyClovek = true;
    		mCernyClovek = false;
    	}
		//	this.mcx = this.mcy = this.mcx = this.mcy  
    	invalidate();
    	tahniPrograme();
    }

}