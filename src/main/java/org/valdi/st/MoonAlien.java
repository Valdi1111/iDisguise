package org.valdi.st;

import java.util.UUID;

public class MoonAlien extends FakePlayerDisguise {
	private static final UUID uuid;
	private static final String value;
	private static final String signature;
	
	static {
		uuid = UUID.randomUUID();

		value = "eyJ0aW1lc3RhbXAiOjE1MzUxNDgyMzM1NjUsInByb2ZpbGVJZCI6ImE5MGI4MmIwNzE4NTQ0ZjU5YmE1MTZkMGY2Nzk2NDkwIiwicHJvZmlsZU5hbWUiOiJJbUZhdFRCSCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFhMGJkNTg0MWE3MjhmYWJiYWI3Nzg2NTU5YjhhM2IwMWMxOWUxMmIzYmVkMzFiMzRjNTJlOWRhMzJhZTcxZCIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19";
		signature = "KE/GU93DEBBh1mp2esK9SZrvT2RBKWGr5c/1TmH3ty4Jm3Eupag2NjDJKwLrLZC3P5ZFLjpFg31IZ59Mule4VtvlBoDI9m+3lJk9dAHkaL9wrWRuRes/jzK1Yux3tGOUwTV5ClQu7RXz8uCmqCbbJGJPh67Vojq6PkIE/yY8NlgUHK26pUTUSaVqYuXI0vxvLu8O2sE23oQZJgle651NFyNd8KnVFC2/ZM5i1Y+smsdvklOfRA64Icw01l7LZlv6WlhRVehChO6+oAKrbBOX4Lj1aS6gD+1mOnX/60pFZ9pecBAq+Is3D4WRe0CZQLoHLBJxWwuwrMwMklhkYkv7MKrk+vSQyMpzO2CeLRXZ5rYlrYAdXvzsCNXm8G4rrST+krpkBJVhBVNB0xhYJ2j0W4EU2IqWCSZt77fr7DVqnun47+ZecB+pTCXZg1WueS/oLGAWqBKJKGuq1wtXsDsxdYD0K8kaSObJJAbWvtvHyG5W/uKXtypSrHJB/xWCiuiHkybDdIlftCE1rF5KzJW8sZNe4DI+DUsEF+qU4BoYIKKFYOfi7ZFTpek+mCLP+rzdUdJ84hUTSEjnCmc3ymQg3mM6/3VZXHRKehuG7iPJExOKI58HHP3zJT7i7gMPOtMHMBmvuIl1W85NbE3CTF/pmneMziARe4SiPO4nr1N5Acg=";
	}

	public MoonAlien() {
		super(uuid, "moonallien", value, signature, "MoonAlien");
	}

}
