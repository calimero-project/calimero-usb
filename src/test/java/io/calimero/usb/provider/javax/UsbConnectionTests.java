/*
    Calimero 2 - A library for KNX network access
    Copyright (c) 2023, 2023 B. Malinowsky

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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.serial.usb.Device;

@DisabledOnOs(OS.MAC)
@DisabledIfEnvironmentVariable(named="CI", matches="true")
class UsbConnectionTests {
	private static final int vendorId = 0x0e77;
	private static final int productId = 0x0104;
	private static final String manufacturer = "weinzierl";
	private static final String product = "KNX-USB";
	private static final String sno = "00C500000011";


	@Test
	void newUsingAny() throws KNXException, InterruptedException {
		final var device = new Device(0, 0);
		open(device);
	}

	@Test
	void newUsingExactMatch() throws KNXException, InterruptedException {
		try (var devices = UsbConnection.attachedKnxDevices()) {
			final var device = devices.findFirst().orElseThrow();
			open(device);
		}
	}

	@ParameterizedTest
	@MethodSource("nonExistingDeviceFactory")
	void noExactMatchFound(final Device device) {
		assertThrows(KNXException.class, () -> open(device));
	}

	private static Stream<Device> nonExistingDeviceFactory() {
		return Stream.of(new Device(vendorId + 1, productId, sno, manufacturer, product),
				new Device(vendorId, productId + 1, sno, manufacturer, product),
				new Device(vendorId, productId, sno + "?", manufacturer, product),
				new Device(vendorId, productId, sno, manufacturer + "?", product),
				new Device(vendorId, productId, sno, manufacturer, product + "?"));
	}

	@Test
	void newUsingVendorProductId() throws KNXException, InterruptedException {
		final var device = new Device(vendorId, productId);
		open(device);
	}

	@Test
	void newUsingManufacturerName() throws KNXException, InterruptedException {
		final var device = new Device(0, 0, "", manufacturer, "");
		open(device);
	}

	@Test
	void newUsingProductName() throws KNXException, InterruptedException {
		final var device = new Device(0, 0, "", "", product);
		open(device);
	}

	@Test
	void newUsingSerialNumber() throws KNXException, InterruptedException {
		final var device = new Device(sno);
		open(device);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0e77:0104", sno, manufacturer, product })
	void newUsingString(final String id) throws KNXException, InterruptedException {
		open(id);
	}

	@Test
	void newUsingEmptyString() throws KNXException {
		try (var __ = new UsbConnection("")) {}
	}

	@Test
	void noMatchUsingString() {
		assertThrows(KNXException.class, () -> { try (var __ = new UsbConnection("invalid")) {} });
	}

	private static void open(final Device device) throws KNXException, InterruptedException {
		try (var c = new UsbConnection(device)) {
			c.deviceDescriptor();
		}
	}

	private static void open(final String id) throws KNXException, InterruptedException {
		try (var c = new UsbConnection(id)) {
			c.deviceDescriptor();
		}
	}
}
