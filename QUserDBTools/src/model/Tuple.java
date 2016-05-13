/*******************************************************************************
 * QBiC User DB Tools enables users to add people and affiliations to our mysql user database.
 * Copyright (C) 2016  Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package model;

public class Tuple {

  private Object one;
  private Object two;

  public Tuple(Object one, Object two) {
    this.one = one;
    this.two = two;
  }

  public Object getOne() {
    return one;
  }

  public Object getTwo() {
    return two;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((one == null) ? 0 : one.hashCode());
    result = prime * result + ((two == null) ? 0 : two.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tuple other = (Tuple) obj;
    if (one == null) {
      if (other.one != null)
        return false;
    } else if (!one.equals(other.one))
      return false;
    if (two == null) {
      if (other.two != null)
        return false;
    } else if (!two.equals(other.two))
      return false;
    return true;
  }


}
