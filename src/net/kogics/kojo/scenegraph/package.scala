/*
 * Copyright (C) 2015 Lalit Pant <pant.lalit@gmail.com>
 *
 * The contents of this file are subject to the GNU General Public License
 * Version 3 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.gnu.org/copyleft/gpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package net.kogics.kojo

package object scenegraph {

  trait AnyDrawing

  trait VectorDrawing extends AnyDrawing

  trait RasterDrawing extends AnyDrawing

  trait Picture[+T <: AnyDrawing]

  type AnyPicture = Picture[AnyDrawing]
  type VectorPicture = Picture[VectorDrawing]
  type RasterPicture = Picture[RasterDrawing]

  //  trait Picture {
  //    type T
  //  }
  //
  //  trait AnyPicture extends Picture {
  //    type T = AnyDrawing
  //  }
  //
  //  trait VectorPicture extends Picture {
  //    type T = VectorDrawing
  //  }
  //
  //  trait RasterPicture extends Picture {
  //    type T = RasterDrawing
  //  }

}
