package info.faljse.SDNotify.jni;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *  Copyright (C) 2016 Zsombor Welker, zsombor.welker@webvalto.hu
 *
 *  @see <a href="https://github.com/faljse/SDNotify">https://github.com/faljse/SDNotify</a>
 *  @author <a href="mailto:zsombor.welker@webvalto.hu">Zsombor Welker</a>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
public interface CLibrary extends Library {
    int getpid();

    CLibrary LIBRARY = (CLibrary) Native.loadLibrary("c", CLibrary.class);
}
