/*
    Calimero 2 - A library for KNX network access
    Copyright (c) 2015, 2023 B. Malinowsky

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Linking this library statically or dynamically with other modules is
    making a combined work based on this library. Thus, the terms and
    conditions of the GNU General Public License cover the whole
    combination.

    As a special exception, the copyright holders of this library give you
    permission to link this library with independent modules to produce an
    executable, regardless of the license terms of these independent
    modules, and to copy and distribute the resulting executable under terms
    of your choice, provided that you also meet, for each linked independent
    module, the terms and conditions of the license of that module. An
    independent module is a module which is not derived from or based on
    this library. If you modify this library, you may extend this exception
    to your version of the library, but you are not obligated to do so. If
    you do not wish to do so, delete this exception statement from your
    version.
*/

package io.calimero.usb.provider.javax;

import java.io.ByteArrayOutputStream;
import java.util.EnumSet;

import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.KNXIllegalArgumentException;

final class HidReportHeader {
	/*
	  HID report header structure

	          -----------------------------------------
	  Field   | Report ID | Packet Info | Data length |
	          |           | seq  | type |             |
	  		  -----------------------------------------
	  Size    |     1     |      1      |     1       |
	*/

	// bit is set 1 to indicate respective packet type
	// valid packet types are in the range [3..6], all other combinations are not allowed
	public enum PacketType {
		Start(1), End(2), Partial(4);

		private final int id;
		PacketType(final int id) { this.id = id; }

		int id() { return id; }
	}

	// USB 1.1 full speed: max length of interrupt pipes is 64 bytes
	private static final int maxFrameSize = 64;

	private static final int headerSize = 3;
	// USB report ID fixed to 1 for KNX communication
	private static final int reportId = 0x01;

	// first packet has sequence number 1 (0 is reserved)
	private final int seqNo;
	private final EnumSet<PacketType> type;
	private final int length;


	static HidReportHeader from(final byte[] frame, final int offset) throws KNXFormatException {
		if (frame.length - offset < headerSize)
			throw new KNXFormatException("frame to short to fit HID report header");
		final int id = frame[offset] & 0xff;
		if (id != reportId)
			throw new KNXFormatException("not a KNX USB report (wrong report ID " + id + ")");
		final int info = frame[offset + 1] & 0xff;
		try {
			final int seqNo = info >> 4;
			final var type = parseType(info & 0xf);
			final int length = frame[offset + 2] & 0xff;
			return new HidReportHeader(seqNo, type, length);
		}
		catch (final KNXIllegalArgumentException e) {
			throw new KNXFormatException(e.getMessage());
		}
	}

	HidReportHeader(final int sequence, final EnumSet<PacketType> type, final int dataLength) {
		seqNo = validateSequence(sequence);
		this.type = validatePacketType(type);
		length = validateDataLength(dataLength);
	}

	/** @return the report ID, fixed to 1 for KNX communication */
	int reportId() {
		return reportId;
	}

	/** @return the sequence number */
	int sequenceNumber() {
		return seqNo;
	}

	/** @return the packet type */
	EnumSet<PacketType> packetType() {
		return type;
	}

	/** @return the data length */
	int dataLength() {
		return length;
	}

	int structLength() {
		return headerSize;
	}

	byte[] toByteArray(final ByteArrayOutputStream os) {
		os.write(reportId);
		int info = seqNo << 4;
		for (final PacketType t : type)
			info += t.id;
		os.write(info);
		os.write(length);
		return os.toByteArray();
	}

	@Override
	public String toString() {
		return "Report ID " + reportId + " seq " + seqNo + " " + type + " data length " + length;
	}

	private static int validateSequence(final int seq) {
		if (seq < 1 || seq > 5)
			throw new KNXIllegalArgumentException("sequence number " + seq + " not in [1..5]");
		return seq;
	}

	private static EnumSet<PacketType> validatePacketType(final EnumSet<PacketType> t) {
		if (t.size() > 2)
			throw new KNXIllegalArgumentException("invalid packet type " + t);
		return t;
	}

	private static EnumSet<PacketType> parseType(final int t) {
		final EnumSet<PacketType> set = EnumSet.allOf(PacketType.class);
		set.removeIf(packetType -> (t & packetType.id()) == 0);
		return set;
	}

	private static int validateDataLength(final int l) {
		if (l < 0 || l > (maxFrameSize - headerSize))
			throw new KNXIllegalArgumentException("data length " + l + " not in [0..61]");
		return l;
	}
}
