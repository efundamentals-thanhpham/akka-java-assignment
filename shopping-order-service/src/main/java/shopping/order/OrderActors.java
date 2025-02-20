package shopping.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import shopping.order.dto.OrderRequest;
import shopping.order.dto.OrderResponse;
import shopping.order.service.OrderService;

public class OrderActors extends AbstractBehavior<OrderActors.Command> {

	private static final Logger logger = LoggerFactory.getLogger(OrderActors.class);
	// tao ra mot thang nhu nay gui di
	public static final class CreateOrder implements Command {
		public final OrderRequest orderRequest;
		public final ActorRef<ActionPerformed> replyTo;

		public CreateOrder(OrderRequest orderRequest, ActorRef<ActionPerformed> replyTo) {
			this.orderRequest = orderRequest;
			this.replyTo = replyTo;
		}
	}

	public static final class GetOrder  implements Command{
		public final String orderId;
		public final ActorRef<ActionPerformed> replyTo;
		public GetOrder(String orderId,ActorRef<ActionPerformed> replyTo) {
			this.orderId = orderId;
			this.replyTo = replyTo;
		}
	}
	public static final class UpdateOrder implements Command {
		public final String id;
		public final OrderRequest orderRequest;
		public final ActorRef<ActionPerformed> replyTo;

		public UpdateOrder(String id, OrderRequest orderRequest, ActorRef<ActionPerformed> replyTo) {
			this.id = id;
			this.orderRequest = orderRequest;
			this.replyTo = replyTo;
		}
	}
	public static final class ActionPerformed implements Command {
		public final OrderResponse orderResponse;

		public ActionPerformed(OrderResponse orderResponse) {
			this.orderResponse = orderResponse;
		}
	}

	private final OrderService orderService;

	interface Command {
	}

	private OrderActors(ActorContext<Command> context, OrderService orderService) {
		super(context);
		this.orderService = orderService;
	}

	// create order va tra ve
	private Behavior<Command> onCreateOrder(CreateOrder createOrder) {
		createOrder.replyTo.tell(new ActionPerformed(orderService.createOrder(createOrder.orderRequest)));
		return this;
	}

	private Behavior<Command> onGetOrder(GetOrder getOrder) {
		getOrder.replyTo.tell(new ActionPerformed(orderService.getOrderById(getOrder.orderId)));
		return this;
	}

	private Behavior<Command> onUpdateOrder(UpdateOrder updateOrder) {
		updateOrder.replyTo.tell(new ActionPerformed(orderService.updateOrder(updateOrder.id, updateOrder.orderRequest)));
		return this;
	}

	public static Behavior<Command> create(OrderService orderService) {
		return Behaviors.setup(ctx -> {
			return new OrderActors(ctx, orderService);
		});
	}
	// Message gui den day
	@Override
	public Receive<Command> createReceive() {
		System.out.println("Create message received");
		return newReceiveBuilder()
				.onMessage(CreateOrder.class, this::onCreateOrder)
				.onMessage(GetOrder.class, this::onGetOrder)
				.onMessage(UpdateOrder.class, this::onUpdateOrder)
				.build();
	}

}
